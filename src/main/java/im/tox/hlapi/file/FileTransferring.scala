package im.tox.hlapi.file

import im.tox.hlapi.traits._
import im.tox.hlapi.core._

class FileTransferring[T <: AbstractFile] {
  def proposeFile(file: T, user: User) : OutgoingTransfer[T] = ???
}
