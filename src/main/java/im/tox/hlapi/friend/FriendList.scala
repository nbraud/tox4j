package im.tox.hlapi.friend

import im.tox.hlapi.core._

class FriendList(req: FriendListReq) extends Iterable[User] {
  def add(user: User, noSPAM: NoSpam, message: String) { ??? }
  def add(address: ToxAddress, nick: Option[String], message: String) { ??? }
  def add(address: ToxAddress, message: String) { add(address, None, message) }
  private[friend] def addNoRequest(user: User) { ??? }

  def delete(user: User) { ??? }

  def iterator: Iterator[User] = ???
}
