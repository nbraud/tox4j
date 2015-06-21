package im.tox.hlapi.core.settings

import scala.collection.GenTraversable
import im.tox.hlapi.core.ToxState

/**
  * Abstract class implemented by configurable modules.
  *
  * In particular, all [[im.tox.hlapi.core.ToxModules]] and [[ToxConfig]]
  * are configurable in this way.
  */
abstract class Configurable {
  /** The type which describes the various settings */
  type SettingKey <: SettingKeyTrait

  /** To each [[SettingKey]] there is a matching lens. */
  def getSetting(key: SettingKey): ToxState => key.V
  def setSetting(key: SettingKey)(value: key.V): ToxState => ToxState
}
