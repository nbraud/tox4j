package im.tox.hlapi.message

import im.tox.hlapi.core.User

import com.github.nscala_time.time.Imports._

abstract class Message {
  val id: MessageId
  val from: User

  // Actually, this should be a compat lib for java.date
  val time: DateTime
}
