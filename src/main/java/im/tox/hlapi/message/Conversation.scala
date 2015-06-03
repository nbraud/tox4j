package im.tox.hlapi.message

import scala.concurrent.Future

import im.tox.hlapi.util.Stream
import im.tox.hlapi.core._

abstract class Conversation {
  def sendMessage(message: Message)(tox: ToxState): (ToxState, Future[Unit]) = ???
  def typing(isTyping: Boolean)(tox: ToxState): ToxState = ???
  def messageStream(tox: ToxState): (ToxState, Stream[MessageEvent]) = ???
  val id: ConversationId
}
