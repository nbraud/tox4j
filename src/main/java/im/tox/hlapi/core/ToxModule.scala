package im.tox.hlapi.core

import scalaz._
import scalaz.syntax.either._

trait ToxModule {
  type State
  def initial: State
  def name: String = getClass.getName

  type ImplType
  private[hlapi] def impl(lens: Lens[ToxState, State]): ImplType

  // A module which registers callback should override this
  def register(tox: ToxState): \/[String, (ToxState, ImplType)] =
    tox.stateLens(this)(initial) match {
      case Some((tox, lens)) => \/-((tox, impl(lens)))
      case None              => -\/(name)
    }

}
