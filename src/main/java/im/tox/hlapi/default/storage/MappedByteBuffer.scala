package im.tox.hlapi.default.storage

trait MappedByteBuffer extends java.nio.MappedByteBuffer with FileLike {
  def flush = force

  def set(i: Integer, v: Byte) = put(i, v)
}
