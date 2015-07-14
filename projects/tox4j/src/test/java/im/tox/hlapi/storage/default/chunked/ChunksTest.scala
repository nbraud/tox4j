package im.tox.hlapi.storage.default.chunked

import org.scalacheck._

import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

final class ChunksTest extends FlatSpec with GeneratorDrivenPropertyChecks {
  private val genSize: Gen[ChunkSize] = {
    for {
      size <- Gen.choose(0, 32 * 1024 * 1024)
    } yield ChunkSize(size)
  }

  private def genChunks(implicit chunkSize: ChunkSize): Gen[Chunks] = {
    for {
      offset <- Gen.choose(0, Int.MaxValue / chunkSize.value) map (_ * chunkSize.value)
      size <- Gen.choose(0, (Int.MaxValue - offset) / chunkSize.value) map (_ * chunkSize.value)
    } yield Chunks.byRawSize(offset, size)
  }

  forAll(genSize) { implicit chunkSize =>
    forAll(genChunks) { chunks =>
      assert(chunks.start % chunkSize.value == 0)
      assert(chunks.end % chunkSize.value == 0)

      /* TODO(nbraud) Add more tests */
    }

    forAll(Gen.choose(0, Int.MaxValue / 2)) { size =>
      val chunks = Chunks.bySize(offset = 0, size)
      assert(chunks.size * (chunkSize.value - 1) >= size)
    }
  }

}
