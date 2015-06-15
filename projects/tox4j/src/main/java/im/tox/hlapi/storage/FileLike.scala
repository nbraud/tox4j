package im.tox.hlapi.storage

import scala.collection.{ Seq, GenTraversable }

trait FileLike {
  // TODO(nbraud) Need proper error monad

  /**
   * The type of Slices.
   *
   * Slices represent a contiguous part of a `FileLike`, and are
   *  handles used for reading from and writing to files.
   * Their size must be expressible as a signed [[Int]], as such it
   *  cannot exceed 2GB.
   */
  type Slice <: SliceBase

  /**
   * [[SliceBase]] is the interface all [[Slice]] types must implement.
   *
   * It contains immutable `offset` and `size` fields.
   */
  protected abstract class SliceBase(
    final val offset: Long,
    final val size: Int
  )

  /**
   * Obtain a [[Slice]] that represents part of the file.
   *
   * `offset` and `sliceSize` must be positive, and
   *  `offset + sliceSize <= size`.
   */
  def slice(offset: Long, sliceSize: Int): Option[Slice]

  /**
   * The file's size, in bytes.
   */
  def size: Long

  /**
   * Writes pending changes to persistent storage.
   *
   * All write operations performed before a `flush()` MUST be written to
   *  persistent storage. They MAY be shadowed by later `set()` to the same
   *  location.
   */
  def flush(slice: Slice): Unit

  /**
   * Writes a [[Seq[Byte]] to the slice.
   *
   * @return false if the write failed because of
   *  invalid arguments (`offset + data.size > slice.size`).
   */
  def writeSeq(slice: Slice)(offset: Int, data: Seq[Byte]): Boolean

  /**
   * Reads the whole slice.
   *
   * The slice is traversed lazily.
   * Concurrent write to it (or overlapping slices) MAY appear in the
   * data that is read.
   */
  def readSeq(slice: Slice): GenTraversable[Byte]

  /**
   * Writes a single [[Byte]] to the slice.
   *
   * @return false if the write failed because
   *  `offset >= slice.size`.
   */
  def writeByte(slice: Slice)(offset: Int, value: Byte): Boolean

  /**
   * Reads a single [[Byte]] from the slice.
   *
   * @return None if the read failed because
   *  `offset >= slice.size`.
   */
  def readByte(slice: Slice)(offset: Int): Option[Byte]
}
