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

  trait Impl {
    def lookup(conversation: ConversationId)(tox: ToxState): GenTraversable[Message] = ???
    def search(query: Query)(tox: ToxState): GenTraversable[Message] = ???
  }
}
