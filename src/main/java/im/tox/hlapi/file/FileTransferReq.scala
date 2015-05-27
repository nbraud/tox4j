package im.tox.hlapi.file

import im.tox.hlapi.core._
import im.tox.hlapi.storage.KeyValue

trait FileTransferReq {
  type T <: AbstractFile
  def callback(newTransfer: IncomingTransfer[T])(tox: ToxState): ToxState
  def state: KeyValue[TransferId, Transfer[T]]
}
