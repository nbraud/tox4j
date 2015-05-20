package im.tox.hlapi.log

import im.tox.hlapi.message.{ ConversationId, Message }

class Logging {
  def lookup(conversation: ConversationId): Iterable[Message] = ???

  def search(query: Query): Iterable[Message] = ???
}
