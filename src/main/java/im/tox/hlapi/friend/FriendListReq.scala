package im.tox.hlapi.friend

import im.tox.hlapi.core._
import im.tox.hlapi.storage.KeyValue

trait FriendListReq {
  def callback(newRequest: IncomingRequest)
  val state: State

  trait State extends KeyValue[PublicKey, User]
}
