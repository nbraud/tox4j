package im.tox.hlapi.message

import scala.concurrent.Future

import im.tox.hlapi.core._

abstract class ConversationId extends Serializable {
  val key: PublicKey
  val name: String
}
