package im.tox.hlapi.friend

import im.tox.hlapi.core._
import im.tox.hlapi.storage.KeyValue

trait FriendListReq {
  def callback(newRequest: IncomingRequest)(tox: ToxState): ToxState
  def storage: KeyValue[PublicKey, User]
}
