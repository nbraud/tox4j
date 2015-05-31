package im.tox.hlapi.message

import im.tox.hlapi.core._

class TextMessaging extends ToxModule {
  type State = Unit
  def initial = ()

  type ImplType = Impl
  private[hlapi] object impl extends Impl

  trait Impl {
    def startConversation(user: User)(tox: ToxState): (ToxState, UserConversation) = ???
  }
}
