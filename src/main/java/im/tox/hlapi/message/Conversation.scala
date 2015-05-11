package im.tox.hlapi.message

import im.tox.hlapi.util.Stream
import im.tox.hlapi.message.Message

abstract class Conversation {
  def sendMessage(msg: Message) : Future[Unit] = ???
  def typing(b: Bool) : Future[Unit] = ???
  val msgStream : Stream[Message] = ???
}
