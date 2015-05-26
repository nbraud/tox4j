package im.tox.hlapi.core

import scalaz._
import scalaz.syntax.either._

trait ToxModule {
  type State
  def initial: State
  def name: String

  // A module which registers callback should override this
  def register(tox: ToxState): \/[String, ToxState] =
    tox.mapState(this) match {
      case None    => \/-(tox.setState(this)(initial))
      case Some(_) => -\/(name)
    }

}
