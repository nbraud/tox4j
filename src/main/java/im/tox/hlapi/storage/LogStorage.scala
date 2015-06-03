package im.tox.hlapi.storage

import im.tox.hlapi.message.{ ConversationId, Message }

trait LogStorage extends Iterable[ConversationId] {
  def lookup(conversation: ConversationId): Iterable[Message]
  def append(conversation: ConversationId, message: Message)
  def modify(conversation: ConversationId, message: Message)

  def delete(conversation: ConversationId)
  def delete(conversation: ConversationId, message: Message)
}
