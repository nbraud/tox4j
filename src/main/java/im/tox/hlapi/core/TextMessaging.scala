package im.tox.hlapi.core

import im.tox.hlapi.message.UserConversation

class TextMessaging {
  def startConversation(user: User): UserConversation = ???
  // Might be better to just provide a startConversation function
  //   rather than an object
}
