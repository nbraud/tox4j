package im.tox.hlapi.message

import im.tox.hlapi.core._

final case class TextMessage(
  val msg: String,
  val id: MessageId,
  val from: User,
  val time: java.util.Date
) extends Message
