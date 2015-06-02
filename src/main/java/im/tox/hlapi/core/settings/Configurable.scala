package im.tox.hlapi.core.settings

import scala.collection.GenTraversable
import im.tox.hlapi.core.ToxState

trait Configurable {
  type SettingKey <: SettingKeyTrait

  def settings: GenTraversable[SettingKey]

  trait Impl {
    def getSetting(key: SettingKey): ToxState => key.V
    def setSetting(key: SettingKey)(value: key.V): ToxState => ToxState
  }
}
