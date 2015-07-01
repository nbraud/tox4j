package im.tox.hlapi.storage

import java.io.{ File, RandomAccessFile }

object TempMappedFile {
  /**
   * Create a temporary MappedFile of a given length.
   *
   * The file is guaranteed to be unique,
   * and will be deleted when the JVM exits.
   */
  def apply(size: Long): MappedFile = {
    val file = File.createTempFile("tox4j-FileLike", size.toString)
    file.deleteOnExit()
    val randomFile = new RandomAccessFile(file, "rw")
    randomFile.setLength(size)
    new MappedFile(randomFile.getChannel)
  }
}
