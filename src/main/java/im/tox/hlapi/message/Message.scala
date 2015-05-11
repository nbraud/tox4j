package im.tox.hlapi.message

abstract class Message {
  val from: User
  val time: Nothing // Need to pick a datatype for time
}
