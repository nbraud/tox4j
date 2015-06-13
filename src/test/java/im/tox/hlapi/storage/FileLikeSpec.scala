package im.tox.hlapi.storage

import scala.collection.immutable.{ HashMap, Map }

import org.scalacheck._
import org.scalacheck.commands._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.PropertyChecks

object FileSpecification extends Commands {

  type Sut = MappedByteBuffer

  final case class State(val size: Long, val values: HashMap[Long, Byte]) {
    def get(position: Long): Option[Byte] = values.get(position)
    def set(position: Long, value: Byte): State =
      copy(values = values + ((position, value)))
  }

  def genInitialState: Gen[State] = {
    for {
      keys <- Gen.nonEmptyContainerOf[List, Long](Gen.choose(0, 2 * 1024 * 1024 * 1024))
      values <- Gen.containerOfN[List, Byte](keys.size, arbitrary[Byte])
      val _size = 10.toLong.max(keys.max)
      size <- Gen.choose(_size + 1, 2 * _size)
    } yield State(size, HashMap(keys.zip(values): _*))
  }

  def initialPreCondition(s: State) = true
  def canCreateNewSut(newState: State, initStuts: Traversable[State],
    runningStuts: Traversable[Sut]) = true
  def newSut(state: State) = MappedByteBuffer.tmp(state.size.toInt)

  def destroySut(sut: Sut) = ()

  /* XXXTODO: Check that any sequence of read/write to any number of
   * (potentially overlapping) slices (in a single thread, over the same
   * FileLike) produce the same trace as the equivalent read/write sequence
   * on the model.
   */

  final case class Get(position: Long) extends SuccessCommand {
    type Result = Option[Byte]
    def nextState(s: State) = s

    def preCondition(s: State) = true
    def postCondition(s: State, r: Option[Byte]) = {
      r match {
        case None => Prop(false)
        case Some(_) =>
          Prop(s.get(position) == r || s.get(position) == None)
      }
    }

    def run(sut: Sut) = {
      val slice = sut(position, 1).orNull
      slice.get(0)
    }
  }

  final case class Set(position: Long, value: Byte) extends SuccessCommand {
    type Result = Boolean
    def nextState(s: State) = s.set(position, value)

    def preCondition(s: State) = true
    def postCondition(s: State, r: Boolean) = Prop(r)

    def run(sut: Sut) = {
      val slice = sut(position, 1).orNull
      slice.set(0, value)
    }

  }

  final case class Overlap(
      position: Long,
      length1: Int, overlap: Int, length2: Int
  ) extends SuccessCommand {
    type Result = (Byte, Byte, Byte, Byte)
    def nextState(s: State) =
      s.set(position + length1 - overlap, 0xCA.toByte)
        .set(position + length1 - overlap + 1, 0xFE.toByte)

    def preCondition(s: State) = true
    def postCondition(s: State, r: Result) =
      r == (0xCA.toByte, 0xFE.toByte, 0xCA.toByte, 0xFE.toByte)

    def run(sut: Sut) = {
      val slice1 = sut(position, length1).orNull
      val slice2 = sut(position + length1 - overlap, length2).orNull
      slice1.set(length1 - overlap, 0xCA.toByte)
      slice2.set(1, 0xFE.toByte)

      (
        slice1.get(length1 - overlap).getOrElse(0xDE.toByte),
        slice1.get(length1 - overlap + 1).getOrElse(0xAD.toByte),
        slice2.get(0).getOrElse(0xBE.toByte),
        slice2.get(1).getOrElse(0xBE.toByte)
      )
    }
  }

  def genGet(s: State): Gen[Get] = {
    Gen.choose(0, s.size.toInt - 1).map(Get(_))
  }

  def genSet(s: State): Gen[Set] = {
    for (
      position <- Gen.choose(0, s.size - 1);
      value <- arbitrary[Byte]
    ) yield Set(position, value)
  }

  def genOverlap(s: State): Gen[Overlap] = {
    for (
      position <- Gen.choose(0, s.size - 10);
      length1 <- Gen.choose(2, s.size.toInt - position.toInt - 8);
      overlap <- Gen.choose(2, s.size.toInt - position.toInt - 5);
      length2 <- Gen.choose(overlap, s.size.toInt - position.toInt + overlap)
    ) yield Overlap(position, length1, overlap, length2)
  }

  def genCommand(s: State): Gen[Command] =
    Gen.oneOf[Command](genGet(s), genSet(s), genOverlap(s))

}

final class FileLikeSpec extends FlatSpec with PropertyChecks {
  "MappedByteBuffer" should "be a proper FileLike implementation" in {
    FileSpecification.property().check
  }
}
