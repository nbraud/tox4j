package im.tox.hlapi.storage

import org.scalacheck._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

final class TestValueTest extends PickleTest {
  type T = TestValue

  val pickle: Pickle[T] = TestValue.TestPickle
  val genT: Gen[T] = TestValue.gen

  forAll(genT) { (x: T) =>
    pickle.parseFrom(pickle.toByteSeq(x)) should equal(Some(x))
  }

}
