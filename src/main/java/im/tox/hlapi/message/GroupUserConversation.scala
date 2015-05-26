package im.tox.hlapi.message

import im.tox.hlapi.core._
import im.tox.hlapi.group.GroupChat
import im.tox.hlapi.group.GroupUser

final case class GroupUserConversation(val id: GroupUser) extends Conversation {
  val parent: GroupConversation = ???
}
