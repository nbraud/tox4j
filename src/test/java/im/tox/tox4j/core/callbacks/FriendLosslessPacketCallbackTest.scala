package im.tox.tox4j.core.callbacks

import im.tox.tox4j.AliceBobTestBase
import im.tox.tox4j.AliceBobTestBase.ChatClient
import im.tox.tox4j.AliceBobTestBase.ChatClient.Task
import im.tox.tox4j.annotations.NotNull
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.exceptions.ToxException

import org.junit.Assert.assertEquals

class FriendLosslessPacketCallbackTest extends AliceBobTestBase {

  @NotNull
  override def newAlice(): ChatClient = {
    new Client()
  }

  class Client extends ChatClient {

    override def friendConnectionStatus(friendNumber: Int, @NotNull connection: ToxConnection) {
      if (connection != ToxConnection.NONE) {
        debug("is now connected to friend " + friendNumber)
        addTask(new Task() {
          @throws(classOf[ToxException])
          override def perform(@NotNull tox: ToxCore) {
            val packet = ("_My name is " + getName()).getBytes()
            packet(0) = 160.toByte
            tox.sendLosslessPacket(friendNumber, packet)
          }
        })
      }
    }

    override def friendLosslessPacket(friendNumber: Int, @NotNull packet: Array[Byte]) {
      val message = new String(packet, 1, packet.length - 1)
      debug("received a lossless packet[id=" + packet(0) + "]: " + message)
      assertEquals(ChatClient.FRIEND_NUMBER, friendNumber)
      assertEquals(160.toByte, packet(0))
      assertEquals("My name is " + getFriendName, message)
      finish()
    }

  }

}
