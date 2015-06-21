package im.tox.hlapi.core

import im.tox.hlapi.message.TextMessage
import im.tox.hlapi.core.settings._

sealed trait OptimizationTarget
final case object Memory extends OptimizationTarget
final case object Battery extends OptimizationTarget
final case object Performance extends OptimizationTarget

sealed trait ToxConfig extends SettingKeyTrait
final case object Target extends ToxConfig {
  type V = OptimizationTarget
  val default = Battery
}

final case object AwayMessage extends ToxConfig {
  type V = TextMessage
  val default = ???
}

object ToxConfig extends Configurable {
  type SettingKey = ToxConfig

  def getSetting(key: ToxConfig): ToxState => key.V = ???
  def setSetting(key: ToxConfig)(value: key.V): ToxState => ToxState = ???
}
