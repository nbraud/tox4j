package im.tox.hlapi.storage.default.keyvalue

import im.tox.hlapi.storage._
import im.tox.hlapi.storage.IOError._
import im.tox.hlapi.storage.default.chunked._

import im.tox.hlapi.util.IntPickle
import im.tox.hlapi.util.IntPickle._

import scala.collection.immutable.Iterable
import scalaz._
import scalaz.syntax._

object KeyValueLog {
  private final val magicHeader: Int = 0x0BADCAFE
  private final val headerSize: Int = 2 * IntPickle.bytesInInt
  private final val defaultChunkSize: Int = 512

  /**
   * Creates a KeyValueLog in a file, erasing previously-stored data.
   *
   * Takes the chunk size in bytes as an optional parameter,
   *  defaults at 512B.
   */
  def create[K, V <: ValueType[K]](file: FileLike, chunkSize: Int = defaultChunkSize)(
    implicit
    pickle: Pickle[V]
  ): \/[IOError, KeyValueLog[K, V, file.Slice]] = {
    for {
      _ <- IOError.require(chunkSize > 1)
      _ <- file.unsafeResize(headerSize) // Reduce the file to the header
      header <- file.slice(0, headerSize) // Get the header as a slice
      _ <- file.writeSeq(header)(0, magicHeader.toByteSeq) // Write the magic constant
      _ <- file.writeSeq(header)(IntPickle.bytesInInt, chunkSize.toByteSeq) // then the chunk size
      _ <- file.flush(header) // Now, flush
      kv <- open[K, V](file) // Parse and return the constructed object
    } yield kv
  }

  /** Reads an existing KeyValueLog file */
  def open[K, V <: ValueType[K]](file: FileLike)(
    implicit
    pickle: Pickle[V]
  ): \/[IOError, KeyValueLog[K, V, file.Slice]] = {
    for {
      // The file must be at least `headerSize` long.
      _ <- IOError.require(file.size >= headerSize, InvalidFormat)

      // Parse the header.
      header <- file.slice(0, headerSize) flatMap file.readSeq
      magic <- IntPickle.parseFrom(header.take(IntPickle.bytesInInt)).toError
      rawChunkSize <- IntPickle.parseFrom(header.drop(IntPickle.bytesInInt)).toError

      // Check that the header is well-formed.
      _ <- IOError.require(magic == magicHeader, InvalidFormat)
      _ <- IOError.require(rawChunkSize > 1, InvalidFormat)

      // Check that the file contains an exact number of chunks.
      _ <- IOError.require((file.size - headerSize) % rawChunkSize == 0, InvalidFormat)

      // Parse the log itself
      tail <- file.slice(headerSize, file.size.toInt - headerSize)
      logSlice <- KeyValueLogSlice.parse[K, V](file, rawChunkSize)(tail)
    } yield new KeyValueLog[K, V, file.Slice](file, rawChunkSize, logSlice)
  }
}

/**
 * A [[KeyValue]] implementation, fully based on [[KeyValueLogSlice]].
 *
 * This implementation has sub-optimal performance, and cannot contain more than
 *  2GB of data.
 */
final class KeyValueLog[K, V <: ValueType[K], FileSlice <: FileLike#Slice] private (
  private[default] val file: FileLike { type Slice = FileSlice },
  rawChunkSize: Int,
  var logSlice: KeyValueLogSlice[K, V, FileSlice]
)(implicit pickle: Pickle[V])
    extends KeyValue[K, V] with Iterable[V] {
  private implicit val chunkSize = ChunkSize(rawChunkSize)

  /**
   * Adds a new record in the key-value store.
   *
   * If the key is already bound, the older record is removed.
   * If the log is full, the file is grown so that 150 new chunks are free afterwards.
   */
  override def add(obj: V): \/[IOError, Unit] = {
    logSlice.add(file)(obj) match {
      case -\/(OutOfSpace(length)) =>
        val newLength = file.size.toInt + length + 150 * rawChunkSize
        val headerSize = KeyValueLog.headerSize
        for {
          _ <- file.extend(newLength)
          newSlice <- file.slice(headerSize, newLength - headerSize)
          _ <- logSlice.unsafeChangeSlice(newSlice)
          _ <- logSlice.add(file)(obj)
        } yield ()

      case x => x
    }
  }

  override def delete(id: K): \/[IOError, Boolean] = logSlice.delete(file)(id)
  override def lookup(id: K): Option[V] = logSlice.lookup(id)
  override def iterator: Iterator[V] = logSlice.iterator
}
