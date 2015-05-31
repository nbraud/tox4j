package im.tox.hlapi.core

import scalaz._
import scalaz.syntax.either._

trait ToxModule {
  type State
  def initial: State
  def name: String = getClass.getName

  type ImplType
  private[hlapi] def impl: ImplType

  // A module which registers callback should override this
  def register(tox: ToxState): \/[String, (ToxState, ImplType)] =
    tox.mapState(this) match {
      case None    => \/-((tox.setState(this)(initial), impl))
      case Some(_) => -\/(name)
    }

}
