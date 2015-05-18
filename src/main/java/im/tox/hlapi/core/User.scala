package im.tox.hlapi.core

import im.tox.hlapi.message.ConversationId

class User extends ConversationId {
  val key:  PublicKey = ???
  val name: String    = ???
}
