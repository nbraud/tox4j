package im.tox.hlapi.storage

import scala.collection.immutable.{ Map, Set }
import scalaz._, Scalaz._

object KeyValueWrapper {
  def apply[Key, Value <: ValueType[Key]](
    storage: KeyValue[Key, Value]
  ): KeyValueWrapper[Key, Value] = {
    KeyValueWrapper(storage, Map.empty, Set.empty)
  }
}

/**
 * Immutable wrapper for [[KeyValue]] stores.
 *
 * The [[KeyValueWrapper]] delays operations
 *  until [[unsafePerformIO]] is called.
 */
private[hlapi] final case class KeyValueWrapper[Key, Value <: ValueType[Key]] private (
    storage: KeyValue[Key, Value],
    pendingAdd: Map[Key, Value],
    pendingDel: Set[Key]
) {
  // TODO(nbraud) Make GenTraversable (or finer type)

  /** Adds a [[Value]] in the store. */
  def add(obj: Value): KeyValueWrapper[Key, Value] = {
    this.copy(
      pendingAdd = pendingAdd + ((obj.key, obj)),
      pendingDel = pendingDel - obj.key
    )
  }

  /** Looks up a value in the store. */
  def lookup(id: Key): Option[Value] = {
    if (pendingDel(id)) {
      None
    } else {
      pendingAdd.get(id) match {
        case Some(x) => Some(x)
        case None    => storage.lookup(id)
      }
    }
  }

  /** Deletes a value from the store. */
  def delete(id: Key): (KeyValueWrapper[Key, Value], Boolean) = {
    if (pendingDel(id)) {
      (this, false)
    } else if (pendingAdd.contains(id)) {
      (this.copy(
        pendingAdd = pendingAdd - id,
        pendingDel = pendingDel + id
      ), true)
    } else if (storage.lookup(id) == None) {
      (this, false)
    } else {
      (this.copy(pendingDel = pendingDel + id), false)
    }
  }

  /** Deletes a value from the store. */
  def delete(obj: Value): (KeyValueWrapper[Key, Value], Boolean) = {
    delete(obj.key)
  }

  /**
   * Commits pending operations to disk.
   *
   * Calling this makes all other [[KeyValueWrapper]] referring to the same
   *  [[KeyValue]] invalid, and using them has undefined behaviour from now on.
   */
  def unsafePerformIO(): \/[IOError, KeyValueWrapper[Key, Value]] = {
    for {
      _ <- pendingAdd.values.toList traverseU_ { v => storage.add(v) }
      _ <- pendingDel traverseU_ { k => storage.delete(k) }
    } yield this.copy(pendingAdd = Map.empty, pendingDel = Set.empty)
  }
}
