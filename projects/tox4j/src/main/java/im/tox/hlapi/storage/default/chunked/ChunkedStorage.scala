package im.tox.hlapi.storage.default.chunked

import im.tox.hlapi.storage._

import scala.annotation.tailrec
import scala.collection.GenTraversable
import scala.collection.immutable.Iterable
import scalaz._, Scalaz._
import scalaz.std.iterable._

/** The [[Tags]] used in the binary format */
private object Tags {
  final val empty = 0x00.toByte
  final val first = 0xF0.toByte
  final val middle = 0xAA.toByte
  final val last = 0x0F.toByte
  final val single = 0xFF.toByte
}

/** [[Chunk]] contains some parsing helpers. */
private object Chunk {
  type Bytes = Seq[Byte]

  /** Match some [[Bytes]] with a tag */
  private def aux(tag: Byte)(bytes: Bytes): Option[Bytes] = {
    bytes.headOption match {
      case Some(`tag`) => Some(bytes.tail)
      case _           => None
    }
  }

  /** Pattern-match empty chunks */
  object Empty {
    def unapply(bytes: Bytes): Option[Bytes] = aux(Tags.empty)(bytes)
  }

  /** Pattern-match the first chunk of a record */
  object First {
    def unapply(bytes: Bytes): Option[Bytes] = aux(Tags.first)(bytes)
  }

  /** Pattern-match a chunk in the middle of a record */
  object Middle {
    def unapply(bytes: Bytes): Option[Bytes] = aux(Tags.middle)(bytes)
  }

  /** Pattern-match the last chunk of a record */
  object Last {
    def unapply(bytes: Bytes): Option[Bytes] = aux(Tags.last)(bytes)
  }

  /** Pattern-match a single-chunk record */
  object Single {
    def unapply(bytes: Bytes): Option[Bytes] = aux(Tags.single)(bytes)
  }
}

private[default] object ChunkedStorage {
  def parse(file: FileLike, rawChunkSize: Int)(slice: file.Slice): \/[IOError, (ChunkedStorage[file.Slice], GenTraversable[(Chunks, Seq[Byte])])] = {
    implicit val chunkSize = ChunkSize(rawChunkSize)
    val chunks = Chunks.bySize(0, slice.size)
    for {
      _ <- IOError.require(rawChunkSize > 1)
      _ <- IOError.require(slice.size % rawChunkSize == 0)
      rawBytes <- file.readSeq(slice)
      chunked = rawBytes.toIterator.grouped(rawChunkSize).zip(chunks.iterator)

      // TODO(nbraud) I can't even ...
      stupidity <- IOError.wrap { seek(Set.empty, Map.empty, chunked) }
      free = stupidity._1
      records = stupidity._2
    } yield (new ChunkedStorage(slice, rawChunkSize, free), records)
  }

  /**
   * Internal parsing function.
   *
   * Consumes chunks forming a record, then calls back into [[seek]].
   * If it encounters chunks that do not form a valid record,
   *  they are considered to be free.
   */
  @tailrec
  private def consume(
    free: Set[Chunks],
    records: GenTraversable[(Chunks, Seq[Byte])],
    bytes: Iterator[(Seq[Byte], Int)],
    data: Seq[Byte],
    start: Int
  )(implicit chunkSize: ChunkSize): (Set[Chunks], GenTraversable[(Chunks, Seq[Byte])]) = {
    if (bytes.hasNext) {
      val (chunk, offset) = bytes.next

      chunk match {
        case Chunk.Last(chunkData) => {
          val chunks = Chunks.byEnd(start, offset + chunkSize.value)
          seek(
            free,
            records ++ Seq((chunks, data ++ chunkData)),
            bytes
          )
        }

        case Chunk.Middle(chunkData) =>
          consume(free, records, bytes, data ++ chunkData, start)

        case _ => {
          seek(
            free + Chunks.byEnd(start, offset),
            records,
            Seq((chunk, offset)).iterator ++ bytes
          )
        }
      }
    } else {
      (free, records)
    }
  }

  /**
   * Internal parsing function.
   *
   * Seeks forward until it encounters a multi-chunks record.
   * If it encounters chunks that do not form a valid record,
   *  they are considered to be free.
   */
  @tailrec
  private def seek(
    free: Set[Chunks],
    records: GenTraversable[(Chunks, Seq[Byte])],
    bytes: Iterator[(Seq[Byte], Int)]
  )(implicit chunkSize: ChunkSize): (Set[Chunks], GenTraversable[(Chunks, Seq[Byte])]) = {
    if (bytes.hasNext) {
      val (chunk, offset) = bytes.next
      val currentChunk = Chunks.bySize(offset, chunkSize.value)
      chunk match {
        case Chunk.First(data) =>
          consume(free, records, bytes, data, offset)
        case Chunk.Single(data) => {
          seek(
            free,
            records ++ Seq((currentChunk, data)),
            bytes
          )
        }

        case _ =>
          seek(freeChunk(free, currentChunk), records, bytes)
      }
    } else {
      (free, records)
    }
  }

  /**
   * Marks some [[Chunks]] as free in a [[Set[Chunk]]].
   *
   * Merges adjacent free chunks upon insertion.
   */
  private def freeChunk(free: Set[Chunks], chunk: Chunks)(implicit chunkSize: ChunkSize) = {
    val (free1, left) =
      free
        .find(_.end == chunk.start)
        .map { x => (free - x, x.start) }
        .getOrElse((free, chunk.start))
    val (free2, right) =
      free1
        .find(_.start == chunk.end)
        .map { x => (free - x, x.end) }
        .getOrElse((free, chunk.end))

    free2 + Chunks.byEnd(left, right)
  }
}

private[default] final class ChunkedStorage[FileSlice <: FileLike#Slice] private (
    var slice: FileSlice,
    val rawChunkSize: Int,
    var free: Set[Chunks]
) {
  private implicit val chunkSize = ChunkSize(rawChunkSize)
  /**
   * Changes the slice currently in use.
   *
   * Completely unsafe, only meant for implementing [[KeyValueLog]] resizing.
   */
  private[default] def unsafeChangeSlice(slice: FileSlice): \/[IOError, Unit] = {
    if (this.slice.offset == slice.offset && slice.size % rawChunkSize == 0) {
      assert(this.slice.size <= slice.size) // TODO(nbraud) Support shrinking
      val newChunks = Chunks.byEnd(this.slice.size, slice.size)
      free = ChunkedStorage.freeChunk(free, newChunks)
      this.slice = slice
      \/-(())
    } else {
      -\/(InvalidArgument)
    }
  }

  /**
   * The type of files compatible with the slice.
   *
   * It needs to be structural, so that the user of `KeyValueLogSlice` can
   *  provide the matching [[FileLike]] rather than keeping a reference here.
   */
  private type File = FileLike { type Slice = FileSlice }

  /**
   * Grabs a range of free [[Chunks]] with given size, or returns `OutOfSpace`.
   *
   * The returned range is removed from [[free]].
   */
  private def allocate(size: Int): \/[IOError, Chunks] = {
    val numChunks = Chunks.chunkNumber(size)
    free
      .find(_.size >= numChunks)
      .map { chunk =>
        free = free - chunk
        val alloc = Chunks.bySize(chunk.start, rawChunkSize * numChunks)
        if (chunk.size > numChunks) {
          val freed = Chunks.byEnd(alloc.end, chunk.end)
          free = ChunkedStorage.freeChunk(free, freed)
        }
        \/-(alloc)
      }.getOrElse(-\/(OutOfSpace(size)))
  }

  def add(file: File)(data: Seq[Byte]): \/[IOError, Chunks] = {
    for {
      chunks <- allocate(data.size)
      // First write the data itself ...
      chunkedData = {
        data
          .grouped(rawChunkSize - 1)
          .zip(chunks.iterator)
          .toIterable // TODO(nbraud) This can be more efficient
      }
      _ <- chunkedData traverseU_ {
        case (data, chunk) => file.writeSeq(slice)(chunk + 1, data)
      }

      // ... then the tags
      _ <- writeTags(file, chunks)
    } yield chunks
  }

  private def writeTags(file: File, chunks: Chunks): \/[IOError, Unit] = {
    if (chunks.tail.isEmpty) { // if the data fits in a single chunk
      for {
        _ <- file.writeByte(slice)(chunks.head, Tags.single)
        _ <- file.flush(slice)
      } yield ()
    } else { // else care must be taken to ensure atomicity
      for {
        _ <- chunks.tail.init.foldLeft(\/-(()): \/[IOError, Unit]) {
          case (res, i) =>
            res flatMap { _ => file.writeByte(slice)(i, Tags.middle) }
        }

        _ <- file.writeByte(slice)(chunks.last, Tags.last)
        _ <- file.flush(slice)

        // Tag FIRST is written last
        _ <- file.writeByte(slice)(chunks.head, Tags.first)
        _ <- file.flush(slice)
      } yield ()
    }
  }

  def delete(file: File)(chunks: Chunks): \/[IOError, Unit] = {
    free = ChunkedStorage.freeChunk(free, chunks)
    chunks.foldLeft(\/-(()): \/[IOError, Unit]) {
      case (result, i) =>
        result flatMap { _ => file.writeByte(slice)(i, Tags.empty) }
    }
  }
}
