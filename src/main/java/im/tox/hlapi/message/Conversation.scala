package im.tox.hlapi.message

import scala.concurrent.Future

import im.tox.hlapi.util.Stream

abstract class Conversation {
  def sendMessage(msg: Message): Future[Unit] = ???
  def typing(b: Boolean): Future[Unit] = ???
  val msgStream: Stream[Message] = ???
  val id: ConversationId
}
