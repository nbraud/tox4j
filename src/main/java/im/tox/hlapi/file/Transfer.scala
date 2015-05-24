package im.tox.hlapi.file

import im.tox.hlapi.core._
import im.tox.hlapi.storage.ValueType

sealed abstract class Transfer[T <: AbstractFile]
    extends ValueType[TransferId] {
  val key: TransferId = ???

  def abort(tox: ToxState): ToxState

  private[file] def resume(file: T, offset: Long)(tox: ToxState): ToxState

  def status(tox: ToxState): TransferStatus = ???
  val user: User = ???
  private[hlapi] def transferId: TransferId = ???
}

final case class IncomingTransfer[T <: AbstractFile]() extends Transfer[T] {
  def start(file: T)(tox: ToxState): ToxState = ???
  def abort(tox: ToxState): ToxState = ???

  private[file] def resume(file: T, offset: Long)(tox: ToxState): ToxState = {
    ???
  }
}

final case class OutgoingTransfer[T <: AbstractFile]() extends Transfer[T] {
  def abort(tox: ToxState): ToxState = ???
  private[file] def resume(file: T, offset: Long)(tox: ToxState): ToxState = {
    ???
  }
}
