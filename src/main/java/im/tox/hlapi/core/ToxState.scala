package im.tox.hlapi.core

import scala.collection.immutable.Map
import scalaz._
import scalaz.syntax.either._

import im.tox.hlapi.message.UserConversation
import im.tox.hlapi.friend.IncomingRequest

object ToxState {
  /** Constructs a new ToxState */
  def apply() = new ToxState(Map.empty, None, None)
}

/**
 * Immutable structure holding the state of HLAPI (including module's state)
 *
 * ToxState is an immutable data structure that represents the state of HLAPI,
 * including the state of modules. It carries the callbacks which ToxInstance
 * calls when it actually performs I/O.
 * It is meant to be used with the State[ToxState, _] monad.
 */
final case class ToxState private (
    moduleStates: Map[ToxModule, Any],
    conversationCallback: Option[UserConversation => ToxState => ToxState],
    friendCallback: Option[IncomingRequest => ToxState => ToxState]
) {
  /**
   * Register a conversation callback
   *
   * For HLAPI's internal use only
   */
  def registerConversation(callback: UserConversation => ToxState => ToxState) =
    conversationCallback match {
      case None    => Some(this.copy(conversationCallback = Some(callback)))
      case Some(_) => None
    }

  /**
   * Register a friend request callback
   *
   * For HLAPI's internal use only
   */
  def registerFriend(callback: IncomingRequest => ToxState => ToxState) =
    friendCallback match {
      case None    => Some(this.copy(friendCallback = Some(callback)))
      case Some(_) => None
    }

  /**
   * Get the state associated with a given module
   *
   * If there is no such state yet, use the module's initial state.
   */
  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf"))
  private def _getState(t: ToxModule)(init: t.State): t.State =
    moduleStates.get(t) match {
      case None    => init

      //This is horrible. Can moduleStates be more precisely typed?
      case Some(x) => x.asInstanceOf[t.State]
    }

  /**
   * Change the state associated with a module
   *
   * Returns a new ToxState
   */
  private def _setState(t: ToxModule)(s: t.State): ToxState =
    this.copy(moduleStates = moduleStates + ((t, s)))

  /**
   * Wraps _getState and _toxState in a lens
   *
   * It is private to ToxState, so that no module can acquire a Lens
   * for another module's state
   */
  private[core] def stateLens(t: ToxModule)(init: t.State): Option[(ToxState, Lens[ToxState, t.State])] =
    moduleStates.get(t) match {
      case None =>
        Some((
          _setState(t)(init),
          Lens.lensu[ToxState, t.State](
            (s, v) => s._setState(t)(v),
            _._getState(t)(init)
          )
        ))

      case Some(_) => None
    }

  /**
   * Register a module
   *
   * This calls the module's register method with appropriate parameters.
   */
  def register(t: ToxModule): \/[String, (ToxState, t.ImplType)] =
    t.register(this)

  /** Configuration for ToxState */
  type SettingKey = ToxConfig
  val settings = ToxConfig.settings
}
