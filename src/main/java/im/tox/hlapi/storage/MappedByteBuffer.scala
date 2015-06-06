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
}
