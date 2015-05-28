package im.tox.hlapi.message

import im.tox.hlapi.core._

class TextMessaging extends ToxModule {
  type State = Unit
  def initial = ()

  def startConversation(user: User)(tox: ToxState): (ToxState, UserConversation) = {
    ???
  }
}
