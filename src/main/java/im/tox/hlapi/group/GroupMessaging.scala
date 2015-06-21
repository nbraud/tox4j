package im.tox.hlapi.group

import im.tox.hlapi.core._
import im.tox.hlapi.core.settings._
import im.tox.hlapi.message.GroupConversation

import scala.concurrent.Future
import scalaz._

class GroupMessaging extends ToxModule {
  type State = Unit
  val initial = ()

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) =
    new Impl(lens)

  final class Impl(lens: Lens[ToxState, State]) extends Configurable {
    def create(tox: ToxState): (GroupChat, ToxState) = ???
    def join(group: GroupChat)(tox: ToxState): (ToxState, Future[GroupConversation]) = ???

    type SettingKey = GroupSetting
    def getSetting(key: SettingKey): ToxState => key.V = ???
    def setSetting(key: SettingKey)(value: key.V): ToxState => ToxState = ???
  }
}

sealed trait GroupSetting extends SettingKeyTrait
