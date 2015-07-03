package im.tox.hlapi.storage

import scala.collection.immutable.{ HashMap, Map }
import scala.collection.GenTraversable
import scalaz._

import org.scalacheck._
import org.scalacheck.commands._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.PropertyChecks

trait FileLikeSpec extends Commands {

  type Sut <: FileLike
  def createRawFile(size: Long): Sut

  final case class State(size: Long, values: Map[Long, Byte]) {
    def get(position: Long): Option[Byte] = values.get(position)
    def set(position: Long, value: Byte): State = {
      copy(values = values + ((position, value)))
    }
  }

  private val maxOffset = 1024L * 1024L
  private val maxSize = 2 * maxOffset

  final override def genInitialState: Gen[State] = {
    for {
      offsets <- Gen.nonEmptyContainerOf[List, Long](Gen.choose(0L, maxOffset))
      values <- Gen.containerOfN[List, Byte](offsets.size, arbitrary[Byte])
      size <- Gen.choose(
        1L + (10L max offsets.max),
        2L * (10L max offsets.max)
      )
    } yield State(size, HashMap(offsets.zip(values): _*))
  }

  override def initialPreCondition(s: State): Boolean = true
  override def canCreateNewSut(newState: State, initSuts: Traversable[State],
    runningSuts: Traversable[Sut]): Boolean = true

  final override def newSut(state: State): Sut = {
    assert(state.size.isValidInt)
    val file = createRawFile(state.size)
    val slice = file.slice(0, state.size.toInt).toOption.get

    state.values.foreach {
      case (position, byte) =>
        assert(position >= 0 && position < state.size)
        file.writeByte(slice)(position.toInt, byte)
    }
    file
  }

  override def destroySut(file: Sut): Unit = ()

  /* TODO(nbraud) Check that any sequence of read/write to any number of
   * (potentially overlapping) slices (in a single thread, over the same
   * FileLike) produce the same trace as the equivalent read/write sequence
   * on the model.
   */

  private final case class ReadByte(position: Long) extends SuccessCommand {
    type Result = \/[IOError, Byte]
    def nextState(state: State): State = state

    def preCondition(state: State): Boolean = {
      assert(position >= 0L && position < state.size)
      true
    }

    def postCondition(state: State, result: Result): Prop = {
      result match {
        case -\/(_) => Prop(false)
        case \/-(x) =>
          Prop(state.get(position).forall(_ == x))
      }
    }

    def run(file: Sut): Result = {
      for {
        slice <- file.slice(position, 1)
        byte <- file.readByte(slice)(0)
      } yield byte
    }
  }

  private final case class WriteByte(position: Long, value: Byte) extends SuccessCommand {
    type Result = \/[IOError, Unit]
    def nextState(state: State): State = {
      state.set(position, value)
    }

    def preCondition(state: State): Boolean = {
      assert(position >= 0L && position < state.size)
      true
    }

    def postCondition(state: State, result: Result): Prop = Prop(result.isRight)

    def run(file: Sut): Result = {
      for {
        slice <- file.slice(position, 1)
        _ <- file.writeByte(slice)(0, value)
        _ <- file.flush(slice)
      } yield ()
    }

  }

  private final case class ReadSeq(position: Long, size: Int) extends SuccessCommand {
    type Result = \/[IOError, GenTraversable[Byte]]
    def nextState(state: State): State = state

    def preCondition(state: State): Boolean = {
      assert(position >= 0L)
      assert(size >= 0)
      assert(position + size <= state.size)
      true
    }

    def postCondition(state: State, result: Result): Prop = {
      result.map {
        _.toIterable.zipWithIndex.forall {
          case (byte, i) =>
            state.get(position + i) match {
              case None       => true
              case Some(data) => data == byte
            }
        }
      }.map(Prop(_)).getOrElse(Prop(false))
    }

    def run(file: Sut): Result = {
      for {
        slice <- file.slice(position, size)
        data <- file.readSeq(slice)
        _ <- IOError.require(data.size == size)
      } yield data
    }
  }

  private final case class WriteSeq(position: Long, size: Int, data: Seq[Byte])
      extends SuccessCommand {
    type Result = \/[IOError, Unit]

    def nextState(state: State): State = {
      data
        .zipWithIndex
        .foldLeft(state) { case (s, (byte, i)) => s.set(position + i, byte) }
    }

    def preCondition(state: State): Boolean = {
      assert(size >= 0 && position >= 0 && position + size <= state.size)
      true
    }

    def postCondition(state: State, result: Result): Prop = Prop(result.isRight)

    def run(file: Sut): Result = {
      for {
        slice <- file.slice(position, size)
        _ <- file.writeSeq(slice)(0, data)
        _ <- file.flush(slice)
      } yield ()
    }
  }

  private final case class Extend(length: Long) extends SuccessCommand {
    type Result = \/[IOError, Unit]

    def nextState(state: State): State = {
      if (length >= state.size) {
        state.copy(size = length)
      } else {
        state
      }
    }

    def preCondition(state: State): Boolean = {
      assert(length >= 0L)
      true
    }

    def postCondition(state: State, result: Result): Prop = {
      if (length >= state.size) {
        Prop(result.isRight)
      } else {
        Prop(result == -\/(InvalidArgument))
      }
    }

    def run(file: Sut): Result = {
      file.extend(length)
    }
  }

  private final case class Overlap(
      position: Long,
      length1: Int, overlap: Int, length2: Int
  ) extends SuccessCommand {
    type Result = \/[IOError, (Byte, Byte, Byte, Byte)]
    def nextState(state: State): State = {
      state
        .set(position + length1 - overlap, 0xCA.toByte)
        .set(position + length1 - overlap + 1, 0xFE.toByte)
    }

    def preCondition(state: State): Boolean = {
      assert(
        position >= 0L && position + length1 + length2 <= state.size + overlap &&
          length1 >= 0 && length1 >= overlap &&
          length2 >= 0 && length2 >= overlap &&
          overlap >= 2
      )
      true
    }

    def postCondition(state: State, result: Result): Prop = {
      Prop(
        result
          .map { _ == ((0xCA.toByte, 0xFE.toByte, 0xCA.toByte, 0xFE.toByte)) }
          .getOrElse(false)
      )
    }

    def run(file: Sut): Result = {
      for {
        slice1 <- file.slice(position, length1)
        slice2 <- file.slice(position + length1 - overlap, length2)
        _ <- file.writeByte(slice1)(length1 - overlap, 0xCA.toByte)
        _ <- file.writeByte(slice2)(1, 0xFE.toByte)

        _ <- file.flush(slice1)
        _ <- file.flush(slice2)

        byte1 <- file.readByte(slice1)(length1 - overlap)
        byte2 <- file.readByte(slice1)(length1 - overlap + 1)
        byte3 <- file.readByte(slice2)(0)
        byte4 <- file.readByte(slice2)(1)
      } yield (byte1, byte2, byte3, byte4)
    }
  }

  protected final def truncateToInt(x: Long): Int = {
    assert(x >= 0)
    (x min Int.MaxValue).toInt
  }

  private def genReadByte(state: State): Gen[Command] = {
    Gen.choose(0L, state.size - 1).map(ReadByte(_))
  }

  private def genWriteByte(state: State): Gen[Command] = {
    for {
      position <- Gen.choose(0L, state.size - 1)
      value <- arbitrary[Byte]
    } yield WriteByte(position, value)
  }

  private def genExtend(state: State): Gen[Command] = {
    for {
      length <- Gen.choose(0L, maxSize)
    } yield Extend(length)
  }

  private def genOverlap(state: State): Gen[Command] = {
    val size = state.size
    for {
      position <- Gen.choose(0L, size - 10L)
      /* Overlap generates a pair of slices, of sizes `length1`
       * and `length2`, which overlap on `overlap` bytes.
       */
      length1 <- Gen.choose(2, truncateToInt(size - position - 8L))
      overlap <- Gen.choose(2, truncateToInt(size - position - 5L) min length1)
      length2 <- Gen.choose(overlap, truncateToInt(size - position - length1 + overlap))
    } yield Overlap(position, length1, overlap, length2)
  }

  protected final def genPositionLength(state: State): Gen[(Long, Int)] = {
    for {
      position <- Gen.choose(0L, state.size - 2L)
      length <- Gen.choose(1, truncateToInt(state.size - position) min 1024)
    } yield (position, length)
  }

  private def genReadSeq(state: State): Gen[Command] = {
    for {
      (position, length) <- genPositionLength(state)
    } yield ReadSeq(position, length)
  }

  private def genWriteSeq(state: State): Gen[Command] = {
    for {
      (position, length) <- genPositionLength(state)
      data <- Gen.containerOfN[Seq, Byte](length, arbitrary[Byte])
    } yield WriteSeq(position, length, data)
  }

  override def genCommand(state: State): Gen[Command] = {
    Gen.oneOf[Command](
      genReadByte(state),
      genWriteByte(state),
      genReadSeq(state),
      genWriteSeq(state),
      genExtend(state),
      genOverlap(state)
    )
  }

}

abstract class FileLikeTest extends FlatSpec {
  type ConcreteFile <: FileLike
  def createFile(size: Long): ConcreteFile

  private object ConcreteSpec extends FileLikeSpec {
    type Sut = ConcreteFile

    def createRawFile(size: Long): Sut = createFile(size)
  }

  "FileLike" should "prevent mixing slices from different files" in {
    val slice = createFile(1024L).slice(0L, 150).toOption.get
    val file = createFile(1024L)
    assertTypeError("file.readByte(slice)")
    assertTypeError("file.writeByte(slice)")
    assertTypeError("file.readSeq(slice)")
    assertTypeError("file.writeSeq(slice)")
    assertTypeError("file.flush(slice)")
  }

  it should "be a proper FileLike implementation" in {
    ConcreteSpec.property().check
  }

  it should "fail to create invalid slices" in {
    val file = createFile(1024L)
    assert(file.slice(0L, -1).isLeft)
    assert(file.slice(1024L, 1).isLeft)
    assert(file.slice(1023L, 2).isLeft)
  }

  it should "invalidate slices upon truncation" in {
    val file = createFile(1024L)
    val slice = file.slice(512L, 512).toOption.get
    assert(file.unsafeResize(768L).isRight)
    assert(file.readSeq(slice).isLeft)
  }
}
