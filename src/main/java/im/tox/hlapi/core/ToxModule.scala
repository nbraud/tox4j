package im.tox.hlapi.core

import scalaz._
import scalaz.syntax.either._

import im.tox.hlapi.core.security.Policy
import im.tox.hlapi.core.settings.Configurable

abstract class ToxModule {
  type State
  val initial: State
  val name: String = getClass.getName
  val policy: Policy = Policy.default

  type ImplType <: Configurable
  private[hlapi] def impl(lens: Lens[ToxState, State]): ImplType

  // A module which registers callback should override this
  private[core] def register(tox: ToxState, lens: Lens[ToxState, State]): \/[String, (ToxState, ImplType)] =
    \/-((tox, impl(lens)))
}
