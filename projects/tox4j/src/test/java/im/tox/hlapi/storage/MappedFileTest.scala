package im.tox.hlapi.storage

import java.io.File

import org.scalacheck._

import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

final class MappedFileTest extends FileLikeTest with GeneratorDrivenPropertyChecks {
  type ConcreteFile = MappedFile
  override def createFile(size: Long): ConcreteFile = TempMappedFile(size)

  "MappedFile" should "prevent mixing slices from different files" in {
    val slice = TempMappedFile(1024L).slice(0L, 150).toOption.get
    val file = TempMappedFile(1024L)
    assertTypeError("file.readByte(slice)")
    assertTypeError("file.writeByte(slice)")
    assertTypeError("file.readSeq(slice)")
    assertTypeError("file.writeSeq(slice)")
    assertTypeError("file.flush(slice)")
  }

  /* TODO(nbraud) MappedFile-specific unit tests
   *              (behaviour on file deletion, ...)
   */

  forAll(Gen.choose(0L, 32 * 1024 * 1024L)) { size =>
    val rawFile = File.createTempFile("tox4j-MappedFile", size.toString)
    rawFile.deleteOnExit()
    assert(MappedFile(rawFile.getPath, size).isRight)
    assert(MappedFile(rawFile.getPath).isRight)
  }
}
