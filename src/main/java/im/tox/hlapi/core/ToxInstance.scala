package im.tox.hlapi.core

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.ToxOptions
import im.tox.tox4j.impl.ToxCoreImpl
import im.tox.tox4j.core.enums.ToxProxyType

class ToxInstance(options: ToxOptions) {
  protected[core] final val tox = new ToxCoreImpl(options)
  private final val thread = new Thread(new Runnable {
    def run {
      while (true) {
        tox.iteration
        Thread sleep tox.iterationInterval
      }
    }
  })

  // XXXTODO: Bad code
  def this() = this(ToxOptions(true, true, ToxProxyType.NONE, None.orNull, 0))

  def performIO(st: ToxState): ToxState = ???
}
