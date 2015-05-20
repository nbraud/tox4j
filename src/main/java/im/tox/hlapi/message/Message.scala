package im.tox.hlapi.message

import im.tox.hlapi.core.User

abstract class Message {
  val from: User = ???
  val time: java.util.Date = ???
  // Actually, this should be a compat lib for java.date
}
