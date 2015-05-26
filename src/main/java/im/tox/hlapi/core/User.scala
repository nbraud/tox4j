package im.tox.hlapi.core

import im.tox.hlapi.message.ConversationId
import im.tox.hlapi.storage.ValueType

class User(val key: PublicKey)
    extends ConversationId with ValueType[PublicKey] {
  val name: String = ???
}
