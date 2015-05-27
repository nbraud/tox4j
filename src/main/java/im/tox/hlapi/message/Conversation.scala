package im.tox.hlapi.message

import scala.concurrent.Future

import im.tox.hlapi.util.Stream
import im.tox.hlapi.core._

abstract class Conversation {
  def sendMessage(msg: Message)(tox: ToxState): (ToxState, Future[Unit]) = ???
  def typing(b: Boolean)(tox: ToxState): ToxState = ???
  val msgStream: Stream[MessageEvent] = ???
  val id: ConversationId
}
