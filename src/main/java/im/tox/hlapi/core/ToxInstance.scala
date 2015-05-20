package im.tox.hlapi.core

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.ToxOptions
import im.tox.tox4j.impl.ToxCoreImpl
import im.tox.tox4j.core.enums.ToxProxyType

import im.tox.hlapi.message.TextMessaging
import im.tox.hlapi.message.TextMessagingReq

class ToxInstance(options: ToxOptions, data: Array[Byte]) {
  protected[core] final val tox = new ToxCoreImpl(options, data)
  private final val thread = new Thread(new Runnable {
    def run {
      while (true) {
        tox.iteration
        Thread sleep tox.iterationInterval
      }
    }
  })

  def this(options: ToxOptions) = this(options, None.orNull)
  def this() = this(new ToxOptions(true, true, ToxProxyType.NONE, None.orNull, 0))

  var textMessagingDep: Option[TextMessagingReq] = None

  @throws(classOf[DoubleRegistration])
  def registerTextMsg(req: TextMessagingReq): TextMessaging = {
    if (textMessagingDep != None) {
      throw new DoubleRegistration("TextMessaging")
    }
    textMessagingDep = Some(req)
    ???
  }
}

final case class DoubleRegistration(component: String)
  extends Exception(component)
