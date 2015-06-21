package im.tox.hlapi.core.settings

import im.tox.hlapi.core.ToxState

import scalaz._
import scalacheck.ScalazProperties
import scalaz.syntax.either._

import org.scalacheck.{ Arbitrary, Gen }
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks._

final class ConfigurableTest(conf: Configurable) extends FlatSpec {

  conf.getClass.getName should "be provide proper Lenses" in {
    def lens(key: conf.SettingKey) = {
      Lens.lensu[ToxState, key.V](
        (s, v) => conf.setSetting(key)(v)(s),
        conf.getSetting(key)
      )
    }
    forAll { c: conf.SettingKey => ScalazProperties.lens.laws(lens(c)) }
  }
}
