package im.tox.hlapi.friend

import im.tox.hlapi.core._
import im.tox.hlapi.core.settings._
import im.tox.hlapi.storage.KeyValueWrapper

import scala.collection.GenTraversable
import scalaz._

class FriendList(req: FriendListReq)
    extends ToxModule {

  type State = KeyValueWrapper[PublicKey, User]
  val initial: State = KeyValueWrapper(req.storage)

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) =
    new Impl {}

  type SettingKey = SyncConfig
  def settings = SyncConfig.settings

  trait Impl {
    def addNoRequest(user: User)(tox: ToxState): ToxState = ???
    def add(user: User, noSpam: NoSpam, message: String)(tox: ToxState): ToxState = ???
    def add(address: ToxAddress, nick: Option[String], message: String)(tox: ToxState): ToxState = ???
    def add(address: ToxAddress, message: String)(tox: ToxState): ToxState = {
      add(address, None, message)(tox)
    }

    def delete(user: User) = ???

    val star: FriendTag = ???
    def lookupTag(name: String)(tox: ToxState): Option[FriendTag] = ???
    def createTag(name: String)(tox: ToxState): (ToxState, Option[FriendTag]) = ???
    def tags(tox: ToxState): GenTraversable[FriendTag] = ???
    def friends(tox: ToxState): GenTraversable[User] = ???

    def getSetting(key: SettingKey): ToxState => key.V = ???
    def setSetting(key: SettingKey)(value: key.V): ToxState => ToxState = ???
  }

}
