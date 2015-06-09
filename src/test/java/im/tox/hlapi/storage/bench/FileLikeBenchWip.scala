package im.tox.hlapi.storage.bench

import im.tox.tox4j.bench.{ Confidence, TimingReport }
import org.scalameter.api._

import java.io.{ File, RandomAccessFile }
import java.nio.channels.FileChannel.MapMode
import java.nio.MappedByteBuffer
import im.tox.hlapi.storage.{ MappedByteBuffer => Buffer }

class FileLikeBench extends TimingReport {
  protected override def confidence = Confidence.high

  val fileLike = (for {
    size <- Gen.range("size (MB)")(1, 25, 1)
  } yield {
    val file = new File(s"/tmp/tox4j-FileLike-$size")
    file.deleteOnExit()
    val file2 = new RandomAccessFile(file, "rw")
    file2.setLength(size * 1024 * 1024)
    Buffer(file2.getChannel().map(MapMode.READ_WRITE, 0, size * 1024 * 1024))
  }).cached


  timing of "FileLike" in {

    measure method "get" in {
      using(fileLike) in (x => {
        for (i <- 0.toLong to x.size - 1) {
          x.get(i)
        }
      })
    }


    measure method "set" in {
      using(fileLike) in (x => {
        for (i <- 0.toLong to x.size - 1) {
          x.set(i, i.toByte)
        }
      })
    }

  }
}
