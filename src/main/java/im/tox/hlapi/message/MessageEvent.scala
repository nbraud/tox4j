package im.tox.hlapi.message

sealed abstract class MessageEvent {
  def id: MessageId
}

final case class NewMessage(message: Message) extends MessageEvent {
  val id = message.id
}

final case class DeleteMessage(val id: MessageId) extends MessageEvent

final case class EditMessage(message: Message) extends MessageEvent {
  val id = message.id
}
