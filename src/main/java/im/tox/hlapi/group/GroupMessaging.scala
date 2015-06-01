package im.tox.hlapi.group

import im.tox.hlapi.core._
import im.tox.hlapi.message.GroupConversation

import scala.concurrent.Future
import scalaz._

class GroupMessaging extends ToxModule {
  type State = Unit
  val initial = ()

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) =
    new Impl {}

  trait Impl {
    def create(tox: ToxState): (GroupChat, ToxState) = ???
    def join(group: GroupChat)(tox: ToxState): (ToxState, Future[GroupConversation]) = ???
  }
}
