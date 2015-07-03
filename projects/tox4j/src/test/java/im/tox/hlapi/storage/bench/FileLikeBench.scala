package im.tox.hlapi.storage.bench

import im.tox.hlapi.storage.{ MappedFile, TempMappedFile }

import im.tox.tox4j.bench.TimingReport
import org.scalameter.api._
import org.scalameter.Log

import scala.collection.immutable.Stream

final class FileLikeBench extends TimingReport {
  private def files(minSize: Int, maxSize: Int) = {
    Gen
      .range("size (KB)")(minSize, maxSize, (maxSize - minSize) / 64 max 10)
      .map { x => TempMappedFile(x.toLong * 1024L) }
      .cached
  }

  private val smallFiles = files(1, 100)
  private val largeFiles = files(512, 2048)

  timing of "FileLike" in {

    measure method "readByte" in {
      using(smallFiles) in { file =>
        val slice = file.slice(0L, file.size.toInt).toOption.get
        for (i <- 0 to file.size.toInt - 1) {
          file.readByte(slice)(i)
        }
      }
    }

    measure method "writeByte" in {
      using(smallFiles) in { file =>
        val slice = file.slice(0L, file.size.toInt).toOption.get
        for (i <- 0 to file.size.toInt - 1) {
          file.writeByte(slice)(i, i.toByte)
        }
      }
    }

    measure method "readSeq" in {
      using(largeFiles) in { file =>
        val slice = file.slice(0L, file.size.toInt).toOption.get
        file.readSeq(slice).foreach(c => ())
      }
    }

    measure method "writeSeq" in {
      using(largeFiles) in { file =>
        val slice = file.slice(0L, file.size.toInt).toOption.get
        // TODO(nbraud) This is waaaay too slow.
        val data = new ZeroSeq(slice.size)
        file.writeSeq(slice)(0, data)
      }
    }
  }
}

private final class ZeroSeq(override val length: Int) extends Seq[Byte] {
  override def apply(idx: Int): Byte = 0.toByte
  override def iterator: Iterator[Byte] = {
    val seqLength = length
    new Iterator[Byte] {
      private var index: Int = 0
      def hasNext: Boolean = index < seqLength
      def next(): Byte = { index = index + 1; 0.toByte }
    }
  }
}
