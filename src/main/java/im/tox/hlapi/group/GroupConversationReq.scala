package im.tox.hlapi.group

import im.tox.hlapi.core._
import im.tox.hlapi.message.GroupUserConversation
import im.tox.hlapi.storage.KeyValue

trait GroupConversationReq {
  def inviteCallback(chat: GroupChat)
  def privateCallback(conversation: GroupUserConversation)

  val state: State
  trait State extends KeyValue[PublicKey, GroupChat]
}
