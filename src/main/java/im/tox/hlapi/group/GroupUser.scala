package im.tox.hlapi.group

import im.tox.hlapi.message.ConversationId
import im.tox.hlapi.core._

class GroupUser extends ConversationId {
  val key:    PublicKey = ???
  val name:   String    = ???
  val parent: GroupChat = ???
}
