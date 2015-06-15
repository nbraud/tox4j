package im.tox.hlapi.storage.bench

import im.tox.tox4j.bench.{ Confidence, TimingReport }
import org.scalameter.api._
import org.scalameter.Log

import java.io.{ File, RandomAccessFile }
import java.nio.channels.FileChannel.MapMode
import im.tox.hlapi.storage.MappedFile

class FileLikeBench extends TimingReport {
  //  protected override def confidence = Confidence.high

  val fileLike =
    Gen.range("size (KB)")(100, 10500, 400).map(MappedFile.tmp).cached

  timing of "FileLike" in {

    measure method "get" in {
      using(fileLike) in (x => {
        val slice = x(0.toLong, x.size.toInt).orNull
        for (i <- 0 to x.size.toInt - 1) {
          slice.get(i)
        }
      })
    }

    measure method "set" in {
      using(fileLike) in (x => {
        val slice = x(0.toLong, x.size.toInt).orNull
        for (i <- 0 to x.size.toInt - 1) {
          slice.set(i, i.toByte)
        }
      })
    }

    measure method "iterate" in {
      using(fileLike) in (x => {
        x(0.toLong, x.size.toInt).orNull.foreach(c => ())
      })
    }

  }
}
