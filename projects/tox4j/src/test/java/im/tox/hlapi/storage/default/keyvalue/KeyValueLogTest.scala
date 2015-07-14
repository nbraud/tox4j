package im.tox.hlapi.storage.default.keyvalue

import scala.collection.immutable.{ HashMap, Map }
import scalaz._

import im.tox.hlapi.storage._
import im.tox.hlapi.storage.TestValue._

import org.scalacheck._
import org.scalacheck.commands._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.PropertyChecks

private final class KeyValueLogSpecification(val file: FileLike)(
    implicit
    pickle: Pickle[TestValue]
) extends KeyValueTest {

  type Key = Int
  type Value = TestValue
  type Sut = KeyValueLog[Key, Value, file.Slice]

  override def canCreateNewSut(newState: State, initSuts: Traversable[State],
    runningSuts: Traversable[Sut]): Boolean = runningSuts.isEmpty

  override def genKey: Gen[Key] = arbitrary[Int]
  override def genValue: Gen[Value] = TestValue.gen

  override def cleanSut(): \/[IOError, Sut] = {
    KeyValueLog.create[Key, Value](file)
  }

  final case object Read extends SuccessCommand {
    type Result = \/[IOError, Iterable[Value]]

    override def preCondition(state: State): Boolean = true
    override def nextState(state: State): State = state

    override def run(keyValueLog: Sut): Result = {
      KeyValueLog.open[Key, Value](file)
    }

    override def postCondition(state: State, result: Result): Prop = {
      result.map { iter =>
        Prop(iter.forall { value => state.get(value.key).contains(value) })
      }.getOrElse(Prop(false))
    }
  }

  override def genCommand(s: State): Gen[Command] = {
    Gen.oneOf[Command](Read, genGet(s), genSet(s))
  }

}

final class KeyValueLogTest extends FlatSpec {
  "KeyValueLog" should "be a proper KeyValue implementation" in {
    val spec = new KeyValueLogSpecification(TempMappedFile(4096L))
    spec.property().check
  }
}
