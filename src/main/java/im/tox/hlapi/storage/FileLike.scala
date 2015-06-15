package im.tox.hlapi.storage

import scala.collection.immutable.Iterable

trait FileLike {
  // Attempt to discard part of the file (e.g using fallocate(2) hole-punching).
  // Failure MUST be handled gracefully by the caller
  //  (i.e. HLAPI storage implementation).
  //  NOTE: Haven't yet found where Java implements this.
  def discard(slice: Slice): Boolean = false

  //TODO Need proper error monad
  def apply(offset: Long, size: Int): Option[Slice]
  def size: Long

}

trait Slice extends Iterable[Byte] {
  def size: Int

  // All write operations performed before a flush() MUST be written to
  //  persistent storage. They MAY be shadowed by later set() to the same
  //  location.
  def flush()

  def write(offset: Int)(data: Array[Byte]): Boolean
  def write(data: Array[Byte]): Boolean = write(0)(data)

  def get(offset: Int): Option[Byte]
  def set(offset: Int, value: Byte): Boolean
}
