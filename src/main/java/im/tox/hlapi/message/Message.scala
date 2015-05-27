package im.tox.hlapi.message

import im.tox.hlapi.core.User

abstract class Message {
  val id: MessageId
  val from: User

  // Actually, this should be a compat lib for java.date
  val time: java.util.Date
}
