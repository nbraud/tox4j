package im.tox.hlapi.message

import im.tox.hlapi.core.User

abstract class Message {
  val from: User
  val time: Nothing // Need to pick a datatype for time
}
