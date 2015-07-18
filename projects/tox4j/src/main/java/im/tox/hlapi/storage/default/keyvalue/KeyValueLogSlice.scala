package im.tox.hlapi.storage.default.keyvalue

import im.tox.hlapi.storage._
import im.tox.hlapi.storage.default.chunked._

import scala.annotation.tailrec
import scala.collection.GenTraversable
import scala.collection.immutable.Iterable
import scalaz._
import scalaz.syntax.either._

/** Functions for parsing and creating a [[KeyValueLogSlice]] */
private[default] object KeyValueLogSlice {
  /** Parse a [[KeyValueLogSlice]] from a [[FileLike#Slice]]. */
  def parse[K, V <: ValueType[K]](file: FileLike, rawChunkSize: Int)(
    slice: file.Slice
  )(implicit pickle: Pickle[V]): \/[IOError, KeyValueLogSlice[K, V, file.Slice]] = {
    for {
      _ <- IOError.require(slice.size % rawChunkSize == 0)
      rawData <- file.readSeq(slice)

      // TODO(nbraud) I can't even ...
      stupidity <- ChunkedStorage.parse(file, rawChunkSize)(slice)
      store = stupidity._1
      elements = stupidity._2

      mapElements <- parseElements[K, V](file)(store, elements)
    } yield new KeyValueLogSlice(store, Map(mapElements: _*))
  }

  private def parseElements[K, V <: ValueType[K]](file: FileLike)(
    store: ChunkedStorage[file.Slice],
    elements: GenTraversable[(Chunks, Seq[Byte])]
  )(implicit pickle: Pickle[V]): \/[IOError, Seq[(K, (Chunks, V))]] = {
    elements.foldLeft(\/-(Seq()): \/[IOError, Seq[(K, (Chunks, V))]]) {
      case (res, (chunk, bytes)) =>
        res flatMap { seq: Seq[(K, (Chunks, V))] =>
          pickle.parseFrom(bytes) match {
            case None      => store.delete(file)(chunk) map { case () => seq }
            case Some(obj) => \/-(seq :+ ((obj.key, (chunk, obj))))
          }
        }
    }
  }
}

/**
 * A key-value log contained in a single [[FileLike#Slice]].
 *
 * This is an implementation class meant for consumption by [[KeyValue]]
 *  implementations, such as [[KeyValueLog]].
 */
private[default] final class KeyValueLogSlice[K, T <: ValueType[K], FileSlice <: FileLike#Slice] private (
    val chunked: ChunkedStorage[FileSlice],
    var map: Map[K, (Chunks, T)]
)(implicit val pickle: Pickle[T]) {
  def iterator: Iterator[T] = map.values.map(_._2).iterator

  /**
   * Changes the slice currently in use.
   *
   * Completely unsafe, only meant for implementing [[KeyValueLog]] resizing.
   */
  private[default] def unsafeChangeSlice(slice: FileSlice): \/[IOError, Unit] = {
    chunked.unsafeChangeSlice(slice)
  }

  /**
   * The type of files compatible with the slice.
   *
   * It needs to be structural, so that the user of `KeyValueLogSlice` can
   *  provide the matching [[FileLike]] rather than keeping a reference here.
   */
  private type File = FileLike { type Slice = FileSlice }

  /** Inserts a new record, or fails with an [[IOError]] */
  def add(file: File)(obj: T): \/[IOError, Unit] = {
    // TODO(nbraud) this is currently not atomic
    for {
      _ <- delete(file)(obj.key)
      chunks <- chunked.add(file)(pickle.toByteSeq(obj))
      _ = { map = map + ((obj.key, (chunks, obj))) }
    } yield ()
  }

  /**
   * Looks up a record from a key.
   *
   * This only uses the in-memory datastructure, and should be fast.
   */
  def lookup(id: K): Option[T] = map.get(id).map { case (_, value) => value }

  /** Deletes a record */
  def delete(file: File)(id: K): \/[IOError, Boolean] = {
    map.get(id) match {
      case None => \/-(false)
      case Some((chunks, _)) =>
        map = map - id // Drop the item from the in-memory store
        chunked.delete(file)(chunks) map { _ => true } // and from disk storage
    }
  }
}
