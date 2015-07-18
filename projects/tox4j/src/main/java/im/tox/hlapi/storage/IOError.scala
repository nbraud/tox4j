package im.tox.hlapi.storage

import java.io.IOException
import scala.util.Try
import scalaz._

sealed trait IOError

/** Denotes an invalid argument documented by the method that returned it. */
case object InvalidArgument extends IOError

/** Denotes a parsing error, including invalid parameters in header. */
case object InvalidFormat extends IOError

/**
 * Denotes an attempt to use a slice invalidated by `file.unsafeResize`.
 *
 * If the slice was invalidated, then the size extended to a larger size,
 * whether this error is returned is implementation-defined.
 */
case object InvalidSlice extends IOError

/** Denotes a failure to allocate more space in the persistent storage. */
final case class OutOfSpace(needed: Int) extends IOError

/**
 * Default error, in case no error more relevant was found.
 *
 * In particular, this is returned when an exception
 * that is not an [[IOException]] was thrown.
 */
case object UnknownFailure extends IOError

/** Wraps an [[IOException]] as an [[IOError]] */
final case class Exception(exception: IOException) extends IOError

/** Helper functions to work with the `\/[IOError, _]` monad. */
object IOError {
  /**
   * Intercept exceptions and converts them to IOError.
   *
   * @returns `-\/(Exception(_))` if an [[IOException]] is intercepted,
   *  else returns `-\/(UnknownFailure)` when intercepting another exception.
   */
  private def recover[A](tryValue: Try[\/[IOError, A]]): \/[IOError, A] = {
    tryValue
      .recover { case exn: IOException => -\/(Exception(exn)) }
      .getOrElse(-\/(UnknownFailure))
  }

  /** Wraps an exception-raising expression into the [[IOError]] monad. */
  def wrap[A](value: => A): \/[IOError, A] = {
    recover(Try(value).map(\/-(_)))
  }

  /**
   * Asserts a condition is verified (or returns an [[IOError]]).
   *
   * This is an [[IOError]] alternative to [[Prelude.require]].
   *
   * @param condition under which the expression is evaluated.
   * @param error returned when [[condition]] is `false`.
   *        Defaults to [[InvalidArgument]].
   *
   * @return `-\/(error)` if the condition is falsified, `\/-(())` otherwise.
   */
  def require(condition: Boolean, error: IOError = InvalidArgument): \/[IOError, Unit] = {
    if (condition) {
      \/-(())
    } else {
      -\/(error)
    }
  }

  /** Implicitely adds a [[toError]] method to [[Option]]. */
  implicit class RichOption[A](option: Option[A]) {
    /**
     * Converts the [[Option]] to an [[IOError]].
     *
     * @return [[UnknownFailure]] if [[option]] was `None`, its value otherwise.
     */
    def toError: \/[IOError, A] = {
      option match {
        case None    => -\/(UnknownFailure)
        case Some(x) => \/-(x)
      }
    }
  }
}
