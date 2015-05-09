package im.tox.tox4j.core.callbacks

import im.tox.tox4j.AliceBobTestBase
import im.tox.tox4j.annotations.NotNull
import im.tox.tox4j.core.enums.ToxConnection

import org.junit.Assert.assertNotEquals

final class ConnectionStatusCallbackTest extends AliceBobTestBase {

  class Client extends AliceBobTestBase.ChatClient {
    override def connectionStatus(@NotNull connection: ToxConnection) {
      super.connectionStatus(connection)
      assertNotEquals(ToxConnection.NONE, connection)
      finish()
    }
  }

  @NotNull
  override def newAlice() = new Client()
}
