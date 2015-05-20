package im.tox.hlapi.file

import im.tox.hlapi.core._
import im.tox.hlapi.storage.KeyValue

trait FileTransferReq[T <: AbstractFile] {
  def callback(newTransfer: IncomingTransfer[T])
  val state: State

  trait State extends KeyValue[TransferId, Transfer[T]]
}
