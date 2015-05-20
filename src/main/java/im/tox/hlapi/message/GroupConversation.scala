package im.tox.hlapi.message

import im.tox.hlapi.core._
import im.tox.hlapi.group.GroupChat
import im.tox.hlapi.group.GroupUser

final case class GroupConversation(group: GroupChat) extends Conversation {
  def members: Iterable[GroupUser] = ???
  val id: GroupChat = group
}
