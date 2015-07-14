package im.tox.hlapi.storage.default.chunked

private[chunked] object Chunks {
  /** Creates a [[Chunks]] range by its start and end offsets. */
  def byEnd(offset: Int, end: Int)(implicit chunkSize: ChunkSize): Chunks = {
    assert(offset >= 0 && end >= offset && chunkSize.value > 1)
    assert(offset % chunkSize.value == 0)
    assert(end % chunkSize.value == 0)

    new Chunks(
      offset,
      end,
      chunkSize
    )
  }

  /** Creates a [[Chunks]] range by its (raw) size. */
  def byRawSize(offset: Int, size: Int)(implicit chunkSize: ChunkSize): Chunks = {
    assert(size >= 0 && size % chunkSize.value == 0)
    assert((offset.toLong + size).isValidInt)

    byEnd(offset, offset + size)
  }

  /** Creates a [[Chunks]] range by the size of the data it contains. */
  def bySize(offset: Int, size: Int)(implicit chunkSize: ChunkSize): Chunks = {
    assert(size >= 0)

    byRawSize(offset, totalSize(size))
  }

  /** Computes the number of required chunks to store `size` bytes. */
  def chunkNumber(size: Int)(implicit chunkSize: ChunkSize): Int = {
    val chunkSizeInt = chunkSize.value
    assert(chunkSizeInt > 1 && size >= 0)

    if (size % (chunkSizeInt - 1) == 0) {
      size / (chunkSizeInt - 1)
    } else {
      (size / (chunkSizeInt - 1)) + 1
    }
  }

  /** Computes the total size used to store `size` bytes. */
  def totalSize(size: Int)(implicit chunkSize: ChunkSize): Int = {
    chunkSize.value * chunkNumber(size)
  }
}

/** Newtype for a [[Range]] of chunks */
private[default] class Chunks private (start: Int, end: Int, chunkSize: ChunkSize)
  extends Range(start, end, chunkSize.value)
