package im.tox.hlapi.file

import im.tox.hlapi.core._
import im.tox.hlapi.storage.{ FileLike, KeyValue }

trait FileTransferReq {
  type T <: FileLike
  def callback(newTransfer: IncomingTransfer[T])(tox: ToxState): ToxState
  def state: KeyValue[TransferId, Transfer[T]]
}
