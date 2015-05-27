package im.tox.hlapi.group

import im.tox.hlapi.core._
import im.tox.hlapi.message.GroupConversation

import scala.concurrent.Future

class GroupMessaging extends ToxModule {
  type State = Unit
  val initial = ()
  val name = "GroupMessaging"

  def create(tox: ToxState): (GroupChat, ToxState) = ???
  def join(group: GroupChat)(tox: ToxState): (ToxState, Future[GroupConversation]) = ???
}
