package im.tox.hlapi.core.settings

/** The interface implemented by setting keys. */
abstract class SettingKeyTrait {
  /** The type of the setting's value */
  type V
  /** The default value */
  val default: V
}
