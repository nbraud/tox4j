package im.tox.hlapi.traits

import im.tox.hlapi.core._
import im.tox.hlapi.group._

trait GroupConversationReq {
  def inviteCallback(chat: GroupChat)
  def privateCallback(conversation: GroupUserConversation)

  val state: State
  trait State extends StateStorage[GroupChat]
}
