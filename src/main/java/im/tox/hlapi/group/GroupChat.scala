package im.tox.hlapi.group

import scala.concurrent.Future
import im.tox.hlapi.message.{ GroupConversation, ConversationId }
import im.tox.hlapi.core._
import im.tox.hlapi.storage.ValueType

sealed trait PrivacySetting
case object Public extends PrivacySetting
case object Private extends PrivacySetting

sealed trait Role
case object Founder extends Role
case object Op extends Role
case object User extends Role
case object Observer extends Role
case object Banned extends Role
case object Invalid extends Role

sealed trait JoinError
case object NickTaken extends JoinError
case object GroupFull extends JoinError
case object InviteDisabled extends JoinError
case object InviteFailed extends JoinError

class GroupChat(_key: PublicKey)
    extends ConversationId with ValueType[PublicKey] {
  val key = _key // Still very ugly. Scalac, you make me sad
  def join(): Future[Either[GroupConversation, JoinError]] = ???

  val name: String = ???
}
