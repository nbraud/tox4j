package im.tox.hlapi.message

trait TextMessagingReq {
  def callback(newConversation: UserConversation)
}
