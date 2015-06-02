package im.tox.hlapi.core

import im.tox.hlapi.core.settings.SettingKeyTrait

import scala.collection.immutable.List

import org.joda.time.Period
import com.github.nscala_time.time.StaticPeriod

object SyncConfig {
  def settings = List[SyncConfig](syncPeriod, syncWhenMobile)
}

sealed trait SyncConfig extends SettingKeyTrait
final case object syncPeriod extends SyncConfig {
  type V = Period
  val default = StaticPeriod.minutes(10)
}

final case object syncWhenMobile extends SyncConfig {
  type V = Boolean
  val default = false
}
