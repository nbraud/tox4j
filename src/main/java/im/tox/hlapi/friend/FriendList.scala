package im.tox.hlapi.friend

import im.tox.hlapi.core._

class FriendList extends Iterable[User] {
  def add(user: User, noSPAM: Integer, message: String) { ??? }
  def add(address: Array[Byte], nick: Option[String], message: String) { ??? }
  def add(address: Array[Byte], message: String) { add(address, None, message) }
  private[friend] def addNoRequest(user: User) { ??? }

  def delete(user: User) { ??? }

  def iterator: Iterator[User] = ???
}
