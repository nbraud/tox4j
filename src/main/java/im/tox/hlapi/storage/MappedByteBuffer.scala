package im.tox.hlapi.storage

import scala.collection.immutable.Iterable

import java.nio.{ MappedByteBuffer => Buffer }
import java.nio.channels.FileChannel.MapMode
import java.io.{ File, RandomAccessFile }

object MappedByteBuffer {
  def apply(buffer: Buffer) = new MappedByteBuffer(buffer)

  def apply(path: String, size: Int) = {
    val file2 = new RandomAccessFile(path, "rw")
    file2.setLength(size * 1024)
    new MappedByteBuffer(file2.getChannel().map(MapMode.READ_WRITE, 0, size * 1024))
  }

  def apply(path: String) = {
    val file = new RandomAccessFile(path, "rw")
    val buffer = file.getChannel().map(MapMode.READ_WRITE, 0, 1024 * 1024)
    new MappedByteBuffer(buffer)
  }

  def tmp(size: Int) = {
    val file = File.createTempFile("/tmp/tox4j-FileLike", "$size")
    file.deleteOnExit()
    val file2 = new RandomAccessFile(file, "rw")
    file2.setLength(size * 1024)
    new MappedByteBuffer(file2.getChannel().map(MapMode.READ_WRITE, 0, size * 1024))
  }

}

final class MappedByteBuffer(buffer: Buffer) extends FileLike {
  def flush() = buffer.force

  def size = buffer.capacity.toLong

  def apply(offset: Long, _size: Int): Option[Slice] =
    if (offset >= Int.MaxValue.toLong || size <= 0) {
      None // TODO: Proper overflow checking & large file handling
    } else if (offset + _size.toLong >= size) {
      Some(new SliceI(offset.toInt, _size))
    } else None

  final case class SliceI(offset: Int, val _size: Int) extends Slice {
    override def size = _size

    def write(off: Int)(data: Array[Byte]) = {
      if (off + data.size <= _size && off >= 0) {
        //TODO: Proper overflow checking
        for (i <- 0 to data.size - 1) {
          buffer.put(offset + off + _size, data(i))
        }
      }
      off + data.size <= _size && off >= 0
    }

    def get(off: Int) =
      if (off <= _size) {
        Some(buffer.get(offset + off))
      } else None

    def set(off: Int, value: Byte) = {
      if (off <= _size) {
        buffer.put(offset + off, value)
      }
      off <= _size
    }

    def iterator = new Iter()

    class Iter extends Iterator[Byte] {
      var index = 0
      def hasNext = index < _size
      def next() = {
        val x = buffer.get(offset + index)
        if (hasNext) { index = index + 1 }
        x
      }
    }
  }
}
