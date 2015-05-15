package im.tox.hlapi.file

import scala.concurrent.Future
import im.tox.hlapi.traits._

trait AbstractFile {
  def read(offset: Long): Stream[Byte]
  def write(offset: Long, data: Stream[Byte]): Future[Unit]
}
