package im.tox.hlapi.storage

import scala.collection.immutable.Iterable
import scala.collection.GenTraversable

import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode
import java.io.{ File, RandomAccessFile }

object MappedFile {
  // TODO(nbraud) As in FileLike, this needs to be brought in an error monad.

  private val rw = "rw"

  /**
   * Creates a MappedFile, given a path, and truncate to the specified length.
   *
   * @param The file path, expressed as a [[String]].
   * @param The new size in bytes, expressed as a [[Long]].
   *  It must be positive (incl. zero).
   */
  def apply(path: String, size: Long): MappedFile = {
    val file = new RandomAccessFile(path, rw)
    file.setLength(size)
    new MappedFile(file.getChannel)
  }

  /**
   * Creates a MappedFile from a path.
   *
   * @param The file path, expressed as a [[String]].
   */
  def apply(path: String): MappedFile = {
    val file = new RandomAccessFile(path, rw)
    new MappedFile(file.getChannel)
  }
}

final class MappedFile(fileChannel: FileChannel) extends FileLike {
  override def size: Long = fileChannel.size

  override def slice(offset: Long, sliceSize: Int): Option[Slice] = {
    // TODO(nbraud) proper overflow checks
    if (sliceSize <= 0 || offset < 0L || offset + sliceSize > size) {
      None
    } else {
      Some(new Slice(
        fileChannel.map(MapMode.READ_WRITE, offset, sliceSize),
        offset,
        sliceSize
      ))
    }
  }

  final class Slice(
    private[MappedFile] val buffer: MappedByteBuffer,
    offset: Long,
    size: Int
  ) extends SliceBase(offset, size)

  override def flush(slice: Slice): Unit = slice.buffer.force

  override def writeSeq(slice: Slice)(offset: Int, data: Seq[Byte]): Boolean = {
    if (offset >= 0 && offset + data.size <= slice.size) {
      // TODO(nbraud) Proper overflow checking
      for ((byte, i) <- data.zipWithIndex) {
        slice.buffer.put(offset + i, byte)
      }
      true
    } else {
      false
    }
  }

  override def readByte(slice: Slice)(offset: Int): Option[Byte] = {
    if (offset < slice.size && offset >= 0) {
      Some(slice.buffer.get(offset))
    } else {
      None
    }
  }

  override def writeByte(slice: Slice)(offset: Int, value: Byte): Boolean = {
    if (offset < slice.size && offset >= 0) {
      slice.buffer.put(offset, value)
      true
    } else {
      false
    }
  }

  override def readSeq(slice: Slice): GenTraversable[Byte] = {
    val fileSlice = slice // Required to avoid name shadowing
    new Iterable[Byte] {
      def iterator = new SliceIterator(fileSlice)
      override def size = fileSlice.size
    }
  }

  private class SliceIterator(slice: Slice) extends Iterator[Byte] {
    private var index = 0 // scalastyle:ignore var.field
    def hasNext: Boolean = index < slice.size
    def next: Byte = {
      val x = slice.buffer.get(index)
      if (hasNext) {
        index = index + 1
      }
      x
    }
  }

}
