package im.tox.hlapi.core.settings

import scala.reflect.Manifest

trait SettingKeyTrait {
  type V
  def default: V

  // Parts that might be used for auto-generating UI
  def valueType: Manifest[V] = ???
  def name: String = getClass.getName.split("\\$").last
  def description: String = "This setting has no description yet"
}
