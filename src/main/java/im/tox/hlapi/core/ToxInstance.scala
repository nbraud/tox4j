package im.tox.hlapi.core

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.ToxOptions
import im.tox.tox4j.impl.ToxCoreImpl
import im.tox.tox4j.core.enums.ToxProxyType

import im.tox.hlapi.message.TextMessaging
import im.tox.hlapi.message.TextMessagingReq

import scalaz._
import scalaz.syntax.id._

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

  def this() = this(new ToxOptions(true, true, ToxProxyType.NONE, None.orNull, 0))

  var textMessagingDep: Option[TextMessagingReq] = None

  def registerTextMsg(req: TextMessagingReq): \/[String, TextMessaging] = {
    textMessagingDep match {
      case Some(_) => "TextMessaging".left
      case None    => textMessagingDep = Some(req); (??? : TextMessaging).right
    }
  }
}
