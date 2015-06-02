package im.tox.hlapi.core

import im.tox.hlapi.message.TextMessage
import im.tox.hlapi.core.settings._

sealed trait OptimizationTarget
final case object Memory extends OptimizationTarget
final case object Battery extends OptimizationTarget
final case object Performance extends OptimizationTarget

sealed trait ToxConfig extends SettingKeyTrait
final case object target extends ToxConfig {
  type V = OptimizationTarget
  val default = Battery
}

final case object awayMessage extends ToxConfig {
  type V = TextMessage
  val default = ???
}

object ToxConfig {
  val settings = List[ToxConfig](target, awayMessage)
}
