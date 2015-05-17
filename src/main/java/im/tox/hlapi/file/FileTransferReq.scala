package im.tox.hlapi.file

import im.tox.hlapi.core._
import im.tox.hlapi.traits.StateStorage

trait FileTransferReq[T <: AbstractFile] {
  def callback(newTransfer: Transfer[T])
  def state: State

  trait State extends StateStorage[Transfer[T]] {
    def delete(state: Transfer[T]): Boolean = delete(state.transferId)
    def add(state: Transfer[T]) { add(state.transferId, state) }
  }
}
