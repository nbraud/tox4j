package im.tox.hlapi.file

import im.tox.hlapi.core._

final case class FileTransferring(req: FileTransferReq) extends ToxModule {
  type State = Unit
  val initial: State = ???

  type ImplType = Impl
  private[hlapi] object impl extends Impl

  trait Impl {
    def proposeFile(file: req.T, user: User)(tox: ToxState): (ToxState, OutgoingTransfer[req.T]) =
      ???
  }
}
