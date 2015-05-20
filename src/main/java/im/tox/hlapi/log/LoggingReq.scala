package im.tox.hlapi.log

import im.tox.hlapi.message.{ConversationId,Message}
import im.tox.hlapi.storage.{FileLike,LogStorage}

trait LoggingReq extends Iterable[ConversationId] {
  val logStore  : LogStorage
  val indexFile : FileLike
}
