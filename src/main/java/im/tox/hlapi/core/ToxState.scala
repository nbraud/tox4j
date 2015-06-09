package im.tox.hlapi.core

import scala.collection.immutable.Map
import scalaz._
import scalaz.syntax.either._

import im.tox.hlapi.message.UserConversation
import im.tox.hlapi.friend.IncomingRequest

object ToxState {
  def apply() = new ToxState(Map.empty, None, None)
}

final case class ToxState private (
    moduleStates: Map[ToxModule, Any],
    conversationCallback: Option[UserConversation => ToxState => ToxState],
    friendCallback: Option[IncomingRequest => ToxState => ToxState]
) {
  def registerConversation(callback: UserConversation => ToxState => ToxState) =
    conversationCallback match {
      case None    => Some(this.copy(conversationCallback = Some(callback)))
      case Some(_) => None
    }

  def registerFriend(callback: IncomingRequest => ToxState => ToxState) =
    friendCallback match {
      case None    => Some(this.copy(friendCallback = Some(callback)))
      case Some(_) => None
    }

  // {map,set}State should probably be lenses
  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf"))
  private def _getState(t: ToxModule)(init: t.State): t.State =
    moduleStates.get(t) match {
      case None    => init

      //This is horrible. Can moduleStates be more precisely typed?
      case Some(x) => x.asInstanceOf[t.State]
    }

  private def _setState(t: ToxModule)(s: t.State): ToxState =
    this.copy(moduleStates = moduleStates + ((t, s)))

  private[core] def stateLens(t: ToxModule)(init: t.State): Option[(ToxState, Lens[ToxState, t.State])] =
    moduleStates.get(t) match {
      case None =>
        Some((
          _setState(t)(init),
          Lens.lensu[ToxState, t.State](
            (s, v) => s._setState(t)(v),
            _._getState(t)(init)
          )
        ))

      case Some(_) => None
    }

  def register(t: ToxModule): \/[String, (ToxState, t.ImplType)] =
    t.register(this)

  type SettingKey = ToxConfig
  val settings = ToxConfig.settings
}
