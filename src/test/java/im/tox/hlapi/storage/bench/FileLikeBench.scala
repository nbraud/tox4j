package im.tox.hlapi.storage.bench

import im.tox.tox4j.Tox4jPerformanceReport
import org.scalameter.api._

import java.io.RandomAccessFile
import java.nio.channels.FileChannel.MapMode
import java.nio.MappedByteBuffer
import im.tox.hlapi.storage.{ MappedByteByffer => Buffer }

class FileLikeBench extends Tox4jPerformanceReport {
  val fileLike = {
    // That's dirty
    val file = new RandomAccessFile("/tmp/tox4j-FileLike-bench", "rw")
    file.setLength(1024 * 1024)
    Buffer(file.getChannel().map(MapMode.READ_WRITE, 0, 1024 * 1024))
  }

  timing of "FileLike" in {
    measure method "get" in {
      using(fileLike) in {
        _.get(10)
      }
    }

    measure method "set" in {
      using(fileLike) in {
        _.get(10, 0x42: Byte)
      }
    }
  }
}
