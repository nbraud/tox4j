package im.tox.hlapi.message

import im.tox.hlapi.core._
import im.tox.hlapi.core.settings._

import scalaz._

class TextMessaging extends ToxModule {
  type State = Unit
  def initial = ()

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) =
    new Impl {}

  sealed trait SettingKey extends SettingKeyTrait
  final case object strictEncoding extends SettingKey {
    type V = Boolean
    val default = true
  }
  final case object downloadInlineMedia extends SettingKey {
    type V = Boolean
    val default = true
  }
  final case object downloadInlineMediaWhenMobile extends SettingKey {
    type V = Boolean
    val default = true
  }
  val settings = List[SettingKey](
    strictEncoding,
    downloadInlineMedia,
    downloadInlineMediaWhenMobile
  )

  trait Impl {
    def startConversation(user: User)(tox: ToxState): (ToxState, UserConversation) = ???

    def getSetting(key: SettingKey): ToxState => key.V = ???
    def setSetting(key: SettingKey)(value: key.V): ToxState => ToxState = ???
  }
}
