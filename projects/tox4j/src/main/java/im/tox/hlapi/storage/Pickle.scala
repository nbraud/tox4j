package im.tox.hlapi.storage

import scala.collection.GenTraversable
import scala.language.postfixOps
import scala.util.Try

import com.trueaccord.scalapb.{
  GeneratedMessage,
  GeneratedMessageCompanion,
  Message
}

import im.tox.hlapi.util.IntPickle
import im.tox.hlapi.util.IntPickle._

/**
 * Trait implemented for serialization.
 *
 * [[Pickle.Protobuf]] provides an implicit to convert ScalaPB
 * Message companion objects to Pickle.
 */
trait Pickle[T] {
  /** Serializes a [[T]] as a [[Seq[Byte]]]. */
  def toByteSeq(obj: T): Seq[Byte]

  /** Attempts to deserialize a [[T]] from a [[Seq[Byte]]]. */
  def parseFrom(bytes: GenTraversable[Byte]): Option[T]
}

object Pickle {
  /** Implicit conversion of ScalaPB companion objects to Pickle. */
  implicit class Protobuf[T <: GeneratedMessage with Message[T]](
      msg: GeneratedMessageCompanion[T]
  ) extends Pickle[T] {
    override def toByteSeq(obj: T): Seq[Byte] = obj.toByteArray

    override def parseFrom(bytes: GenTraversable[Byte]): Option[T] = {
      Try(msg.parseFrom(bytes.toArray)).toOption // TODO(nbraud) avoid array
    }
  }

  /**
   * [[Pickle]] a pair, given [[Pickle]]s for both components.
   *
   * This use length-prefix framing of the left component, and as
   *  such incurrs a 4 bytes overhead. Moreover, the first component
   *  cannot exceed 2GB (after serialization).
   */
  final case class Pair[A, B](pickleA: Pickle[A], pickleB: Pickle[B])
      extends Pickle[(A, B)] {
    override def toByteSeq(x: (A, B)): Seq[Byte] = {
      val (a, b) = x
      val bytesA = pickleA.toByteSeq(a)

      bytesA.size.toByteSeq ++ bytesA ++ pickleB.toByteSeq(b)
    }

    override def parseFrom(bytes: GenTraversable[Byte]): Option[(A, B)] = {
      for {
        length <- IntPickle.parseFrom(bytes.take(IntPickle.bytesInInt))
        a <- pickleA.parseFrom(bytes.drop(IntPickle.bytesInInt).take(length))
        b <- pickleB.parseFrom(bytes.drop(length + IntPickle.bytesInInt))
      } yield (a, b)
    }
  }
}
