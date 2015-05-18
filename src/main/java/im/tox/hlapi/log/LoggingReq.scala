package im.tox.hlapi.log

import im.tox.hlapi.message.{ConversationId,Message}
import im.tox.hlapi.storage.FileLike

trait LoggingReq extends Iterable[ConversationId] {
  def lookup(conversation: ConversationId) : Iterable[Message]
  def append(conversation: ConversationId, msg: Message)
  def modify(conversation: ConversationId, msg: Message)

  def delete(conversation: ConversationId)
  def delete(conversation: ConversationId, msg: Message)

  val indexFile : FileLike
}
