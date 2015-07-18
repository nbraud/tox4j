package im.tox.hlapi.storage

import scala.collection.GenTraversable
import scalaz.\/

/**
 * Interface implemented by [[KeyValue]] stores.
 *
 * This is used by HLAPI to persist module state and configuration.
 * Implementations must provide transactional sementics, even in the case
 *  where the JVM is stopped (crash or power loss) mid-operation.
 */
trait KeyValue[Key, Value <: ValueType[Key]] extends GenTraversable[Value] {
  /**
   * Adds a new record in the key-value store.
   *
   * If the key is already bound, the older record is removed.
   */
  def add(obj: Value): \/[IOError, Unit]

  /** Looks a [[Value]] up from its [[Key]]. */
  def lookup(id: Key): Option[Value]

  /**
   * Deletes an existing binding, based on the [[Key]].
   *
   * Returns `true` iff the [[Key]] was bound.
   */
  def delete(id: Key): \/[IOError, Boolean]

  /**
   * Deletes an existing binding, based on the [[Value]].
   *
   * Returns `true` iff this [[Value]] was bound.
   */
  def delete(obj: Value): \/[IOError, Boolean] = delete(obj.key)
}
