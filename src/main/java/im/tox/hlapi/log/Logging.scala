package im.tox.hlapi.log

import im.tox.hlapi.message.{ ConversationId, Message }
import im.tox.hlapi.core._

import scala.collection.GenTraversable
import scala.concurrent.Future

class Logging {
  def lookup(conversation: ConversationId)(tox: ToxState): GenTraversable[Message] = ???

  def search(query: Query)(tox: ToxState): GenTraversable[Message] = ???
}
