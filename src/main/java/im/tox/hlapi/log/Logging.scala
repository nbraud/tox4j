package im.tox.hlapi.log

import im.tox.hlapi.message.{ ConversationId, Message }
import im.tox.hlapi.core._

import scala.collection.GenTraversable
import scala.concurrent.Future

import scalaz._

class Logging extends ToxModule {
  type State = Unit
  val initial: State = ()

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) =
    new Impl {}

  type SettingKey = SyncConfig
  val settings = SyncConfig.settings

  trait Impl {
    def lookup(conversation: ConversationId)(tox: ToxState): GenTraversable[Message] = ???
    def search(query: Query)(tox: ToxState): GenTraversable[Message] = ???

    def getSetting(key: SettingKey): ToxState => key.V = ???
    def setSetting(key: SettingKey)(value: key.V): ToxState => ToxState = ???
  }
}
