package im.tox.hlapi.core

import scala.option

import im.tox.tox4j.ToxCore
import im.tox.tox4j.ToxCoreImpl
import im.tox.tox4j.core.enums.ToxProxyType

import im.tox.hlapi.traits._
import im.tox.hlapi.message.TextMessaging

class ToxInstance(@NotNull options: ToxOptions, @Nullable data: Array[Byte]) {
  protected[im.tox.hlapi.core] final val tox = new ToxCoreImpl(options,data)
  private final val thread = new Thread(new Runnable {
    def run {
      while true {
        tox.iteration
        Thread sleep tox.iterationInterval
      }
    }
  })

  def this(options: ToxOptions) = this(options, null)
  def this() = this(new ToxOptions(true, true, ToxProxyType.NONE, null, 0))


  var textMessagingDep: Option[TextMessagingReq] = None

  @throw(classOf[DoubleRegistration])
  def registerTextMsg(req: TextMessagingReq): TextMessaging = {
    if textMessagingDep != None {
      throw new DoubleRegistration("TextMessaging")
    }
    textMessagingDep = req
    ???
  }
}


case class DoubleRegistration(component: String) extends Exception(component)
