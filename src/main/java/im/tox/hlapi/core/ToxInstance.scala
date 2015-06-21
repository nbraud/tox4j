package im.tox.hlapi.core

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import im.tox.tox4j.core.enums.ToxProxyType

class ToxInstance(options: ToxOptions) {
  protected[core] final val tox = new ToxCoreImpl(options)
  private final val thread = new Thread(new Runnable {
    def run {
      while (true) { // scalastyle:ignore while
        tox.iterate()
        Thread sleep tox.iterationInterval
      }
    }
  })

  def this() = this(new ToxOptions())

  def performIO(st: ToxState): ToxState = ???
}
