package im.tox.hlapi.file

import im.tox.hlapi.core._

final case class FileTransferring(req: FileTransferReq) {
  def proposeFile(file: req.T, user: User)(tox: ToxState): (ToxState, OutgoingTransfer[req.T]) = ???
}
