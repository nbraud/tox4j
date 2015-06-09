package im.tox.hlapi.core

import scala.collection.immutable.Map
import scalaz._
import scalacheck.ScalazProperties
import scalaz.syntax.either._

import org.scalacheck.{ Arbitrary, Gen }
import org.scalatest._
import org.scalatest.prop.PropertyChecks

final class ToxStateSpec extends FlatSpec with PropertyChecks {

  /*
  "A ToxState" should "fail to register the same module twice" in {
    val state  = ToxState()
    val module = ToxModuleMock()

    for {
      impl1 <- module.register
      impl2 <- module.register
    } yield {
      assert(impl1.isRight)
      impl2 should be -\/("MockModule")
    }
  }
   */

  private class StateModule extends ToxModule {
    type State = Int
    def initial = 0

    type ImplType = Lens[ToxState, Int]
    def impl(x: Lens[ToxState, Int]) = x
  }

  "ToxState" should "provide proper state storage"{

    val state = ToxState()
    val module = new StateModule()
    state.register(module) match {
      //    case -\/(_) => assert(false, "Registration failed")
      case \/-((state2, lens)) =>
        assert(state == state2)
        // Those are the Lens laws from Scalaz
        ScalazProperties.lens.laws(lens)
    }

  }
}
