package im.tox.hlapi.util

import im.tox.hlapi.storage.{ Pickle, PickleTest }

import org.scalacheck._
import org.scalacheck.commands._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.PropertyChecks

final class IntTest extends PickleTest {
  type T = Int

  override val pickle: Pickle[T] = IntPickle
  override val genT: Gen[T] = arbitrary[Int]
}
