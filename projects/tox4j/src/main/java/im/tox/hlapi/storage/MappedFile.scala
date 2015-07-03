package im.tox.hlapi.storage

import scala.collection.immutable.Iterable
import scala.collection.GenTraversable
import scalaz._
import scalaz.syntax.either._

import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode
import java.io.{ File, IOException, RandomAccessFile }

object MappedFile {
  private val rw = "rw"

  /**
   * Creates a MappedFile, given a path, and truncate to the specified length.
   *
   * @param path File path, expressed as a [[String]].
   * @param size New size in bytes, expressed as a [[Long]].
   *             It must be positive (incl. zero).
   */
  def apply(path: String, size: Long): \/[IOError, MappedFile] = {
    IOError.wrap {
      val file = new RandomAccessFile(path, rw)
      file.setLength(size)
      new MappedFile(file)
    }
  }

  /**
   * Creates a MappedFile from a path.
   *
   * @param The file path, expressed as a [[String]].
   */
  def apply(path: String): \/[IOError, MappedFile] = {
    IOError.wrap {
      val file = new RandomAccessFile(path, rw)
      new MappedFile(file)
    }
  }
}

final class MappedFile(file: RandomAccessFile) extends FileLike {
  override def size: Long = file.length

  override def slice(offset: Long, size: Int): \/[IOError, Slice] = {
    // TODO(nbraud) proper overflow checks
    if (size >= 0 && offset >= 0L && offset + size <= this.size) {
      val slice = new Slice(
        file.getChannel.map(MapMode.READ_WRITE, offset, size),
        offset,
        size
      )
      \/-(slice)
    } else {
      -\/(InvalidArgument)
    }
  }

  final class Slice private[MappedFile] (
    private[MappedFile] val buffer: MappedByteBuffer,
    offset: Long,
    size: Int
  ) extends SliceBase(offset, size)

  override def flush(slice: Slice): \/[IOError, Unit] = {
    IOError.wrap { slice.buffer.force }
  }

  override def writeSeq(slice: Slice)(offset: Int, data: Seq[Byte]): \/[IOError, Unit] = {
    for {
      // TODO(nbraud) Proper overflow checking
      _ <- IOError.require(offset >= 0 && offset + data.size <= slice.size)
      _ <- IOError.require(slice.offset + slice.size <= size, InvalidSlice)
      _ <- IOError.wrap {
        data.zipWithIndex.foreach {
          case (byte, i) =>
            slice.buffer.put(offset + i, byte)
        }
      }
    } yield ()
  }

  override def readByte(slice: Slice)(offset: Int): \/[IOError, Byte] = {
    for {
      _ <- IOError.require(offset < slice.size && offset >= 0)
      _ <- IOError.require(slice.offset + slice.size <= size, InvalidSlice)
      byte <- IOError.wrap { slice.buffer.get(offset) }
    } yield byte
  }

  override def writeByte(slice: Slice)(offset: Int, value: Byte): \/[IOError, Unit] = {
    for {
      _ <- IOError.require(offset < slice.size && offset >= 0)
      _ <- IOError.require(slice.offset + slice.size <= size, InvalidSlice)
      _ <- IOError.wrap { slice.buffer.put(offset, value) }
    } yield ()
  }

  override def readSeq(slice: Slice): \/[IOError, GenTraversable[Byte]] = {
    if (slice.offset + slice.size <= size) {
      val fileSlice = slice // Required to avoid name shadowing
      val iterable = new Iterable[Byte] {
        override def iterator = new SliceIterator(fileSlice)
        override def size = fileSlice.size
      }
      \/-(iterable)
    } else {
      -\/(InvalidSlice)
    }
  }

  /* TODO(nbraud) What occurs if the file is truncated *after* readSeq,
   *              but before iterating?
   */
  private class SliceIterator(slice: Slice) extends Iterator[Byte] {
    private var index = 0 // scalastyle:ignore var.field
    override def hasNext: Boolean = index < slice.size
    override def next(): Byte = {
      val x = slice.buffer.get(index)
      if (hasNext) {
        index = index + 1
      }
      x
    }
  }

  override def unsafeResize(size: Long): \/[IOError, Unit] = {
    for {
      _ <- IOError.require(size >= 0)
      _ <- IOError.wrap { file.setLength(size) }
      _ <- IOError.require(this.size == size, UnknownFailure)
    } yield ()
  }
}
