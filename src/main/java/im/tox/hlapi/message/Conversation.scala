package im.tox.hlapi.message

import scala.concurrent.Future

import im.tox.hlapi.util.Stream

abstract class Conversation {
  def sendMessage(msg: Message)(tox: ToxState): (ToxState,Future[Unit]) = ???
  def typing(b: Boolean)(tox: ToxState): ToxState = ???
  val msgStream: Stream[Message] = ???
  val id: ConversationId
}
