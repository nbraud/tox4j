package im.tox.hlapi.storage

import scala.collection.GenTraversable

import org.scalacheck._
import org.scalacheck.Arbitrary._

import im.tox.hlapi.util.IntPickle

final case class TestValue(key: Int, value: Seq[Int]) extends ValueType[Int]

object TestValue {
  private object SeqPickle extends Pickle[Seq[Int]] {
    def parseFrom(bytes: GenTraversable[Byte]): Option[Seq[Int]] = {
      if (bytes.size % IntPickle.bytesInInt != 0) {
        None
      } else {
        Some {
          bytes
            .toIterator
            .grouped(IntPickle.bytesInInt)
            .map(IntPickle.fromBytes)
            .toSeq
        }
      }
    }

    def toByteSeq(ints: Seq[Int]): Seq[Byte] = ints flatMap IntPickle.toByteSeq
  }

  implicit object TestPickle extends Pickle[TestValue] {
    private val pair = Pickle.Pair(IntPickle, SeqPickle)

    def toByteSeq(x: TestValue): Seq[Byte] = pair.toByteSeq((x.key, x.value))
    def parseFrom(bytes: GenTraversable[Byte]): Option[TestValue] = {
      pair
        .parseFrom(bytes)
        .map { case (key, value) => TestValue(key, value) }
    }
  }

  final val gen: Gen[TestValue] = {
    for {
      key <- arbitrary[Int]
      value <- Gen.nonEmptyContainerOf[Seq, Int](arbitrary[Int])
    } yield TestValue(key, value)
  }

}
