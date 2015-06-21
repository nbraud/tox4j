package im.tox.hlapi.message

import im.tox.hlapi.core._
import im.tox.hlapi.core.settings._

import scalaz._

class TextMessaging extends ToxModule {
  type State = Unit
  val initial: State = ()

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) = {
    new Impl(lens)
  }

  final class Impl(lens: Lens[ToxState, State]) extends Configurable {
    def startConversation(user: User)(tox: ToxState): (ToxState, UserConversation) = ???

    type SettingKey = MessageSetting
    def getSetting(key: SettingKey): ToxState => key.V = ???
    def setSetting(key: SettingKey)(value: key.V): ToxState => ToxState = ???
  }
}


sealed trait MessageSetting extends SettingKeyTrait

final case object strictEncoding extends MessageSetting {
  type V = Boolean
  val default = true
}

final case object downloadInlineMedia extends MessageSetting {
  type V = Boolean
  val default = true
}

final case object downloadInlineMediaWhenMobile extends MessageSetting {
  type V = Boolean
  val default = true
}
