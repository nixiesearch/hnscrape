import ai.nixiesearch.hnscrape.{Item, RollingJsonSink}
import cats.effect.IO
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import fs2.Stream
import cats.effect.unsafe.implicits.global
import com.github.luben.zstd.ZstdInputStream
import fs2.io.file.{Files, Path}
import io.circe.Json
import io.circe.parser.*
import fs2.io.readInputStream

import java.io.FileInputStream
import java.nio.file.Files as JFiles

class RollingJsonSinkTest extends AnyFlatSpec with Matchers {
  it should "write jsons to file" in {
    val event = Item(1, Json.obj("id" -> Json.fromInt(1)))
    val dir   = JFiles.createTempDirectory("jsonsink")
    Stream
      .emit[IO, Item](event)
      .through(RollingJsonSink.write(dir, 1024))
      .compile
      .drain
      .unsafeRunSync()
    val content = Files[IO]
      .list(Path.fromNioPath(dir))
      .flatMap(f => readInputStream[IO](IO(new ZstdInputStream(new FileInputStream(f.toNioPath.toFile))), 1024))
      .through(fs2.text.utf8.decode)
      .through(fs2.text.lines)
      .filter(_.nonEmpty)
      .evalMap(line => IO.fromEither(decode[Item](line)))
      .compile
      .toList
      .unsafeRunSync()
    // val content = decode[Item](Files.readString(dir))
    content shouldBe List(event)
  }
}
