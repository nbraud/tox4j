package im.tox.hlapi.friend

import im.tox.hlapi.core._
import im.tox.hlapi.traits.StateStorage

trait FriendListReq {
  def callback(newRequest: IncomingRequest)
  val state: State

  trait State extends StateStorage[User] {
    def delete(user: User): Boolean = delete(???)
    def add(user: User) { add(???, user) }
  }
}
