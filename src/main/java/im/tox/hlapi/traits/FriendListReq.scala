package im.tox.hlapi.traits

import im.tox.hlapi.core._
import im.tox.hlapi.friend._

trait FriendListReq {
  def callback(newRequest: IncomingRequest)
  val state: State

  trait State extends StateStorage[User] {
    def delete(user: User): Boolean = delete(???)
    def add(user: User) { add(???, user) }
  }
}
