package im.tox.hlapi.traits

import im.tox.hlapi.core._
import im.tox.hlapi.file.Transfer
import im.tox.hlapi.file.AbstractFile

trait FileTransferReq[T <: AbstractFile] {
  def callback(newTransfer: Transfer[T])
  def state: State

  trait State extends StateStorage[Transfer[T]] {
    def delete(state: Transfer[T]): Boolean = delete(state.transferId)
    def add(state: Transfer[T]) { add(state.transferId, state) }
  }
}
