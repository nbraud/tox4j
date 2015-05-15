package im.tox.hlapi.storage

trait FileLike {
  // All write operations performed before a flush() MUST be written to
  //  persistent storage. They MAY be shadowed by later set() to the same
  //  location.
  def flush()

  // Attempt to discard part of the file (e.g using fallocate(2) hole-punching).
  // Failure MUST be handled gracefully by the caller
  //  (i.e. HLAPI storage implementation).
  //  NOTE: Haven't yet found where Java implements this.
  def discard(i: Integer, j: Integer): Boolean = false

  def get(i: Integer): Byte
  def set(i: Integer, v: Byte)
}
