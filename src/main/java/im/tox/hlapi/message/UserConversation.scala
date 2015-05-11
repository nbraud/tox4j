package im.tox.hlapi.message

import im.tox.hlapi.message.Conversation
import im.tox.hlapi.core.User

case class UserConversation(user: User) extends Conversation
