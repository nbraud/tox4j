package im.tox.hlapi.core

import scala.collection.immutable.Set
import scalaz._
import scalaz.syntax.either._

import im.tox.hlapi.message.{ TextMessaging, TextMessagingReq }
import im.tox.hlapi.log.{ LoggingReq, Logging }
import im.tox.hlapi.friend.{ FriendListReq, FriendList }
import im.tox.hlapi.file.{ FileTransferReq, FileTransferring }
import im.tox.hlapi.group.{ GroupConversationReq }

final case class ToxState(
    textMessagingDep: Option[TextMessagingReq],
    loggingDep: Option[LoggingReq],
    friendListDep: Option[FriendListReq],
    fileTransferDep: Option[FileTransferReq[_]],
    groupChatDep: Option[GroupConversationReq]
) {
  //  val modules: Set[ToxModule] = Set.empty

  def registerTextMsg(req: TextMessagingReq): \/[String, (ToxState, TextMessaging)] = {
    textMessagingDep match {
      case Some(_) => "TextMessaging".left
      case None    => (this.copy(textMessagingDep = Some(req)), (??? : TextMessaging)).right
    }
  }

  def registerLogging(req: LoggingReq): \/[String, (ToxState, Logging)] = {
    textMessagingDep match {
      case Some(_) => "Logging".left
      case None    => (this.copy(loggingDep = Some(req)), (??? : Logging)).right
    }
  }
}
