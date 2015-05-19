package im.tox.hlapi.file

import im.tox.hlapi.core._
import im.tox.hlapi.storage.KeyValue

trait FileTransferReq[T <: AbstractFile] {
  def callback(newTransfer: IncomingTransfer[T])
  val state: State

  trait State extends KeyValue[Transfer[T]] {
    def delete(state: Transfer[T]): Boolean = delete(state.transferId)
    def add(state: Transfer[T]) { add(state.transferId, state) }
  }
}
