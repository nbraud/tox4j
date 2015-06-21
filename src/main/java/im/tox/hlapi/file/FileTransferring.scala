package im.tox.hlapi.file

import im.tox.hlapi.core._
import im.tox.hlapi.core.settings._

import scalaz._

final case class FileTransferring(req: FileTransferReq) extends ToxModule {
  type State = Unit
  val initial: State = ???

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) = {
    new Impl(lens)
  }

  final class Impl(lens: Lens[ToxState, State]) extends Configurable {
    def proposeFile(file: req.T, user: User)(tox: ToxState): (ToxState, OutgoingTransfer[req.T]) =
      ???

    type SettingKey = FileSetting
    def getSetting(key: FileSetting): ToxState => key.V = ???
    def setSetting(key: FileSetting)(value: key.V): ToxState => ToxState = ???
  }
}

sealed trait FileSetting extends SettingKeyTrait
final case object transferWhenMobile extends FileSetting {
  type V = Boolean
  val default = true
}
final case object simultaneousTransfers extends FileSetting {
  type V = Int
  val default = 5
}
final case object simultaneousTransfersPerUser extends FileSetting {
  type V = Int
  val default = 2
}
final case object suspendTranferUponCongestion extends FileSetting {
  type V = Boolean
  val default = true
}
