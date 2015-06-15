package im.tox.hlapi.storage

import scala.collection.immutable.Iterable

import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode
import java.io.{ File, RandomAccessFile }

object MappedFile {
  def apply(path: String, size: Int) = {
    val file2 = new RandomAccessFile(path, "rw")
    file2.setLength(size * 1024)
    new MappedFile(file2.getChannel())
  }

  def apply(path: String) = {
    val file = new RandomAccessFile(path, "rw")
    new MappedFile(file.getChannel())
  }

  def tmp(size: Int) = {
    val file = File.createTempFile("/tmp/tox4j-FileLike", size.toString)
    file.deleteOnExit()
    val file2 = new RandomAccessFile(file, "rw")
    file2.setLength(size * 1024)
    new MappedFile(file2.getChannel())
  }

}

final class MappedFile(fileChan: FileChannel) extends FileLike {
  def size = fileChan.size

  def apply(offset: Long, _size: Int): Option[Slice] =
    // TODO proper overflow checks
    if (size <= 0 || offset + _size.toLong > size) {
      None
    } else Some(new SliceI(
      fileChan.map(MapMode.READ_WRITE, offset, _size),
      _size
    ))

  private final case class SliceI(
      buffer: MappedByteBuffer,
      _size: Int
  ) extends Slice {
    override val size = _size

    def flush() = buffer.force()

    def write(offset: Int)(data: Array[Byte]) = {
      if (offset + data.size <= _size && offset >= 0) {
        //TODO: Proper overflow checking
        for (i <- 0 to data.size - 1) {
          buffer.put(offset + _size, data(i))
        }
      }
      offset + data.size <= _size && offset >= 0
    }

    def get(offset: Int) =
      if (offset < _size) {
        Some(buffer.get(offset))
      } else None

    def set(offset: Int, value: Byte) = {
      if (offset < _size) {
        buffer.put(offset, value)
      }
      offset < _size
    }

    def iterator = new Iter()

    class Iter extends Iterator[Byte] {
      var index = 0
      def hasNext = index < _size
      def next() = {
        val x = buffer.get(index)
        if (hasNext) { index = index + 1 }
        x
      }
    }
  }
}
