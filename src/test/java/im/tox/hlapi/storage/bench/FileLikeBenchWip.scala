package im.tox.hlapi.storage.bench

import im.tox.tox4j.bench.{ Confidence, TimingReport }
import org.scalameter.api._

import java.io.RandomAccessFile
import java.nio.channels.FileChannel.MapMode
import java.nio.MappedByteBuffer
import im.tox.hlapi.storage.{ MappedByteBuffer => Buffer }

class FileLikeBench extends TimingReport {
  protected override def confidence = Confidence.extreme

  val fileLike = for {
    // That's dirty
    unit <- Gen.unit("")
  } yield {
    val file = new RandomAccessFile("/tmp/tox4j-FileLike-bench", "rw")
    file.setLength(1024 * 1024)
    val buffer = Buffer(file.getChannel().map(MapMode.READ_WRITE, 0, 1024 * 1024))
    buffer
  }

  timing of "FileLike" in {
    measure method "get" in {
      using(fileLike) in {
        _.get(10)
      }
    }

    measure method "get-flush" in {
      using(fileLike) in (x =>
        {
          x.get(10)
          x.flush
        })
    }

    measure method "set" in {
      using(fileLike) in {
        _.set(10, 0x42: Byte)
      }
    }

    measure method "set-flush" in {
      using(fileLike) in (x =>
        {
          x.set(10, 0x42: Byte)
          x.flush
        })
    }
  }
}
