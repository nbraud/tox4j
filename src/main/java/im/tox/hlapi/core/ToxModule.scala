package im.tox.hlapi.core

import scalaz._
import scalaz.syntax.either._

import im.tox.hlapi.core.security.Policy

trait ToxModule {
  type State
  def initial: State
  def name: String = getClass.getName

  def policy: Policy = Policy.default

  type ImplType
  private[hlapi] def impl(lens: Lens[ToxState, State]): ImplType

  // A module which registers callback should override this
  def register(tox: ToxState): \/[String, (ToxState, ImplType)] =
    tox.stateLens(this)(initial) match {
      case Some((tox, lens)) => \/-((tox, impl(lens)))
      case None              => -\/(name)
    }

}
