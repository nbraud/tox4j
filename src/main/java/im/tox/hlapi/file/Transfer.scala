package im.tox.hlapi.file

import im.tox.hlapi.core._

sealed abstract class Transfer[T <: AbstractFile] extends Serializable {
  def abort = ???

  private[file] def resume(file: T, offset: Long)

  def status: Nothing = ???
  val user: User = ???
  private[hlapi] def transferId: Integer = ???
}

case class IncomingTransfer[T <: AbstractFile]() extends Transfer[T] {
  def start(file: T) = ???

  private[file] def resume(file: T, offset: Long) = ???
}

case class OutgoingTransfer[T <: AbstractFile]() extends Transfer[T] {
  private[file] def resume(file: T, offset: Long) = ???
}
