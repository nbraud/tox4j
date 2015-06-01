package im.tox.hlapi.media

import im.tox.hlapi.core.User
import im.tox.hlapi.message.{ Message, MessageId }
import com.github.nscala_time.time.Imports._

final case class SoundMessage(
  val sound: Sound,
  val id: MessageId,
  val from: User,
  val time: DateTime
) extends Message
