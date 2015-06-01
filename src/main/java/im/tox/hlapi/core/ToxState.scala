package im.tox.hlapi.core

import scala.collection.immutable.Map
import scalaz._
import scalaz.syntax.either._

import im.tox.hlapi.message.UserConversation
import im.tox.hlapi.friend.IncomingRequest

// XXXTODO: Make actual constructor private
final case class ToxState(
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
  def mapState(t: ToxModule): Option[t.State] =
    moduleStates.get(t) match {
      case None => None

      //This is horrible. Can moduleStates be more precisely typed?
      case Some(x) => {
        try Some(x.asInstanceOf[t.State])
        catch { case _: Exception => assert(false); None }
      }
    }

  def setState(t: ToxModule)(s: t.State): ToxState =
    this.copy(moduleStates = moduleStates + ((t, s)))

  def register(t: ToxModule): \/[String, (ToxState, t.ImplType)] =
    t.register(this)
}
