package im.tox.hlapi.core

import im.tox.hlapi.message.ConversationId
import im.tox.hlapi.storage.ValueType

class User(_key: PublicKey) extends ConversationId with ValueType[PublicKey] {
  val key: PublicKey = _key // This is embarassingly ugly. Why is it needed?
  val name: String    = ???
}
