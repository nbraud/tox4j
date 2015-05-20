package im.tox.hlapi.message

import im.tox.hlapi.core.User

final case class UserConversation(user: User) extends Conversation {
  val id: User = user
}
