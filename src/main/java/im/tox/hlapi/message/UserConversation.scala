package im.tox.hlapi.message

import im.tox.hlapi.core.User

final case class UserConversation(val id: User) extends Conversation
