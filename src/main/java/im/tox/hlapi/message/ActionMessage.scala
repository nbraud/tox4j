package im.tox.hlapi.message

import im.tox.hlapi.core._

import com.github.nscala_time.time.Imports._

final case class ActionMessage(
  val action: String,
  val id: MessageId,
  val from: User,
  val time: DateTime
) extends Message
