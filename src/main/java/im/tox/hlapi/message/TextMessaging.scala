package im.tox.hlapi.message

import im.tox.hlapi.core._

import scalaz._

class TextMessaging extends ToxModule {
  type State = Unit
  def initial = ()

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) =
    new Impl {}

  trait Impl {
    def startConversation(user: User)(tox: ToxState): (ToxState, UserConversation) = ???
  }
}
