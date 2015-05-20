package im.tox.hlapi.file

import im.tox.hlapi.core._
import im.tox.hlapi.storage.ValueType

sealed abstract class Transfer[T <: AbstractFile]
    extends ValueType[TransferId] {
  val key: TransferId = ???

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
