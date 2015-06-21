package im.tox.hlapi.core.settings

import scala.collection.GenTraversable
import im.tox.hlapi.core.ToxState

abstract class Configurable {
  type SettingKey <: SettingKeyTrait

  def getSetting(key: SettingKey): ToxState => key.V
  def setSetting(key: SettingKey)(value: key.V): ToxState => ToxState
}
