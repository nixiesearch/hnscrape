import ai.nixiesearch.hnscrape.{Item, WorkQueue}
import com.github.luben.zstd.ZstdOutputStream
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.FileOutputStream
import java.nio.file.Files
import io.circe.syntax.*
import fs2.Stream
import cats.effect.unsafe.implicits.global
import io.circe.Json

class WorkQueueTest extends AnyFlatSpec with Matchers {
  it should "load queue from disk" in {
    val dir   = Files.createTempDirectory("queue")
    val file  = Files.createTempFile(dir, "file1", ".jsonl.zst")
    val out   = new ZstdOutputStream(new FileOutputStream(file.toFile))
    val event = Item(1, Json.obj("id" -> Json.fromInt(1)))
    out.write((event.asJson.noSpaces + "\n").getBytes)
    out.close()

    val queue  = WorkQueue.create(dir, 1, 0, 10).unsafeRunSync()
    val result = Stream.fromQueueNoneTerminated(queue).compile.toList.unsafeRunSync()
    result shouldBe List(0, 2, 3, 4, 5, 6, 7, 8, 9)
  }
}
