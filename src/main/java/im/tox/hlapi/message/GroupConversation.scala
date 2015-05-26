package im.tox.hlapi.message

import im.tox.hlapi.core._
import im.tox.hlapi.group.GroupChat
import im.tox.hlapi.group.GroupUser

final case class GroupConversation(val id: GroupChat) extends Conversation {
  def members(tox: ToxState): Iterable[GroupUser] = ???
}
