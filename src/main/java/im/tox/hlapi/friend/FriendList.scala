package im.tox.hlapi.friend

import im.tox.hlapi.core._
import im.tox.hlapi.storage.KeyValueWrapper

import scala.collection.GenTraversable

class FriendList(req: FriendListReq)
    extends ToxModule {

  type State = KeyValueWrapper[PublicKey, User]
  val initial: State = KeyValueWrapper(req.storage)

  type ImplType = Impl
  private[hlapi] object impl extends Impl {
  }

  trait Impl {
    def addNoRequest(user: User)(tox: ToxState): ToxState = ???
    def add(user: User, noSPAM: NoSpam, message: String)(tox: ToxState): ToxState = ???
    def add(address: ToxAddress, nick: Option[String], message: String)(tox: ToxState): ToxState = ???
    def add(address: ToxAddress, message: String)(tox: ToxState): ToxState = {
      add(address, None, message)(tox)
    }

    def delete(user: User) = ???

    def friends: GenTraversable[User] = ???
  }

}
