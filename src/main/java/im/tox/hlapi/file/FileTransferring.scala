package im.tox.hlapi.file

import im.tox.hlapi.core._
import im.tox.hlapi.core.settings._

import scalaz._

final case class FileTransferring(req: FileTransferReq) extends ToxModule {
  type State = Unit
  val initial: State = ???

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) =
    new Impl {}

  sealed trait SettingKey extends SettingKeyTrait
  final case object transferWhenMobile extends SettingKey {
    type V = Boolean
    val default = true
  }
  final case object simultaneousTransfers extends SettingKey {
    type V = Int
    val default = 5
  }
  final case object simultaneousTransfersPerUser extends SettingKey {
    type V = Int
    val default = 2
  }
  final case object suspendTranferUponCongestion extends SettingKey {
    type V = Boolean
    val default = true
  }

  def settings = List[SettingKey](
    transferWhenMobile,
    simultaneousTransfers,
    simultaneousTransfersPerUser,
    suspendTranferUponCongestion
  )

  trait Impl {
    def proposeFile(file: req.T, user: User)(tox: ToxState): (ToxState, OutgoingTransfer[req.T]) =
      ???

    def getSetting(key: SettingKey): ToxState => key.V = ???
    def setSetting(key: SettingKey)(value: key.V): ToxState => ToxState = ???
  }
}
