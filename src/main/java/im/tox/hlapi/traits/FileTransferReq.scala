package im.tox.hlapi.traits

import im.tox.hlapi.core._
import im.tox.hlapi.file.Transfer
import im.tox.hlapi.file.AbstractFile

trait FileTransferReq[T <: AbstractFile] {
  def callback(newTransfer: Transfer[T])
}
