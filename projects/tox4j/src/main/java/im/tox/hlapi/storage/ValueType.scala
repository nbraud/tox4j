package im.tox.hlapi.storage

/**
 * The trait of values in a [[KeyValue]] store.
 *
 * Each value can only have a single associated [[Key]].
 */
trait ValueType[Key] {
  val key: Key
}
