package im.tox.hlapi.storage

trait KeyValue[T <: Serializable] extends Iterable[T] {
  // add should replace existing state with same id
  def add(id: Integer, obj: T)
  def lookup(id: Integer): Option[T]
  def delete(id: Integer) : Boolean
}
