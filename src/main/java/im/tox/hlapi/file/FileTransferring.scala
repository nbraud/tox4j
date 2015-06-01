package im.tox.hlapi.file

import im.tox.hlapi.core._

import scalaz._

final case class FileTransferring(req: FileTransferReq) extends ToxModule {
  type State = Unit
  val initial: State = ???

  type ImplType = Impl
  private[hlapi] def impl(lens: Lens[ToxState, State]) =
    new Impl {}

  trait Impl {
    def proposeFile(file: req.T, user: User)(tox: ToxState): (ToxState, OutgoingTransfer[req.T]) =
      ???
  }
}
