package im.tox.hlapi.traits

import im.tox.hlapi.message.Conversation

trait TextMessagingReq {
  def callback(newConv: Conversation)
}
