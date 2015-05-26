package im.tox.hlapi.log

import im.tox.hlapi.message.{ ConversationId, Message }
import im.tox.hlapi.core._

class Logging {
  def lookup(conversation: ConversationId)(tox: ToxState): Iterable[Message] = ???

  def search(query: Query)(tox: ToxState): Iterable[Message] = ???
}
