package im.tox.hlapi.util

import scala.language.postfixOps
import scala.collection.GenTraversable

import im.tox.hlapi.storage.Pickle

object IntPickle extends Pickle[Int] {
  final val bytesInInt: Int = java.lang.Integer.SIZE / java.lang.Byte.SIZE

  def parseFrom(bytes: GenTraversable[Byte]): Option[Int] = {
    if (bytes.size != bytesInInt) {
      None
    } else {
      Some(fromBytes(bytes))
    }
  }

  // TODO(nbraud) Do something cleaner
  private def toInt(byte: Byte): Int = {
    if (byte >= 0) {
      byte
    } else {
      256 + byte
    }
  }
  /** Parse 4 bytes into an Int */
  def fromBytes(bytes: GenTraversable[Byte]): Int = {
    assert(bytes.size == bytesInInt)

    bytes.foldLeft(0) { case (acc, byte) => (acc << 8) | toInt(byte) }
  }

  def toByteSeq(i: Int): Seq[Byte] = {
    for (j <- 0 until bytesInInt reverse)
      yield ((i >> 8 * j) & 0xFF).toByte
  }

  implicit class RichInt(i: Int) {
    def toByteSeq: Seq[Byte] = IntPickle.toByteSeq(i)
  }
}
