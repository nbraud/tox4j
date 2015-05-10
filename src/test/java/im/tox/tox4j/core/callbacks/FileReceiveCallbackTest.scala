package im.tox.tox4j.core.callbacks

import im.tox.tox4j.AliceBobTestBase
import im.tox.tox4j.AliceBobTestBase.ChatClient
import im.tox.tox4j.AliceBobTestBase.ChatClient.Task
import im.tox.tox4j.annotations.NotNull
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.enums.ToxFileKind
import im.tox.tox4j.exceptions.ToxException

import org.junit.Assert.assertEquals

class FileReceiveCallbackTest extends AliceBobTestBase {

  @NotNull
  override def newAlice(): ChatClient = new Client()

  private class Client extends ChatClient {

    private var fileData: Array[Byte] = null

    @throws(classOf[ToxException])
    override def setup(tox: ToxCore) {
      if (isAlice) {
        fileData = "This is a file for Bob".getBytes
      } else {
        fileData = "This is a file for Alice".getBytes
      }
    }

    override def friendConnectionStatus(friendNumber: Int, @NotNull connection: ToxConnection) {
      if (connection != ToxConnection.NONE) {
        debug("is now connected to friend " + friendNumber)
        assertEquals(ChatClient.FRIEND_NUMBER, friendNumber)
        addTask(new Task() {
          @throws(classOf[ToxException])
          override def perform(@NotNull tox: ToxCore) {
            tox.fileSend(friendNumber, ToxFileKind.DATA, fileData.length, null,
                ("file for " + getFriendName + ".png").getBytes)
          }
        })
      }
    }

    override def fileReceive(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, @NotNull filename: Array[Byte]) {
      debug("received file send request " + fileNumber + " from friend number " + friendNumber)
      assertEquals(ChatClient.FRIEND_NUMBER, friendNumber)
      assertEquals(0 | 0x10000, fileNumber)
      assertEquals(ToxFileKind.DATA, kind)
      if (isAlice) {
        assertEquals("This is a file for Alice".length(), fileSize)
      } else {
        assertEquals("This is a file for Bob".length(), fileSize)
      }
      assertEquals("file for " + getName + ".png", new String(filename))
      finish()
    }
  }

}
