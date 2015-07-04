package im.tox.hlapi.storage

import org.scalacheck._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

abstract class PickleTest extends GeneratorDrivenPropertyChecks with ShouldMatchers {
  type T

  val pickle: Pickle[T]
  val genT: Gen[T]

  forAll(genT) { (x: T) =>
    pickle.parseFrom(pickle.toByteSeq(x)) should equal(Some(x))
  }

}
