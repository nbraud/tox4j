package im.tox.hlapi.storage

import java.nio.{ MappedByteBuffer => Buffer }

object MappedByteBuffer {
  def apply(buffer: Buffer) = new MappedByteBuffer(buffer)
  //  def apply(path: String) = {
  //    val file = new RandomAccessFile(path, "rw")
  //    val buffer = file.getChannel().map(MapMode.READ_WRITE, 0, 1024*1024)
  //    new MappedByteBuffer(buffer)
  //  }
}

final class MappedByteBuffer(buffer: Buffer) extends FileLike {
  def flush() = buffer.force

  def set(i: Long, v: Byte) = buffer.put(i.toInt, v)
  def get(i: Long) = buffer.get(i.toInt)

  def size = buffer.capacity.toLong

  def apply(offset: Long, _size: Int): Option[Slice] =
    if (offset >= Int.MaxValue.toLong || size < 0) {
      None // TODO: Proper overflow checking & large file handling
    } else if (offset + _size.toLong >= size) {
      Some(new SliceI(offset.toInt, _size))
    } else None

  final case class SliceI(offset: Int, override val size: Int) extends Slice {
    def write(off: Int)(data: Array[Byte]) = {
      if (off + data.size <= size && off >= 0) {
        //TODO: Proper overflow checking
        for (i <- 0 to data.size - 1) {
          buffer.put(offset + off + size, data(i))
        }
      }
      off + data.size <= size && off >= 0
    }

    def get(off: Int) =
      if (off <= size) {
        Some(buffer.get(offset + off))
      } else None

    def set(off: Int, value: Byte) = {
      if (off <= size) {
        buffer.put(offset + off, value)
      }
      off <= size
    }

    def iterator = ???

  }
}
