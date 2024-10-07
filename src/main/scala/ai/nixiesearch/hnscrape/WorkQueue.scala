package ai.nixiesearch.hnscrape

import cats.effect.IO
import cats.effect.std.Queue
import com.github.luben.zstd.ZstdInputStream
import fs2.io.file.{Files, Path}
import fs2.Stream

import java.nio.file.Path as JPath
import fs2.io.readInputStream
import io.circe.parser.*

import java.io.FileInputStream

object WorkQueue extends Logging {
  def create(dir: JPath, workers: Int, min: Int, max: Int): IO[Queue[IO, Option[Int]]] = for {
    files <- Files[IO].list(Path.fromNioPath(dir)).compile.toList
    docids <- Stream
      .emits[IO, Path](files)
      .parEvalMapUnordered(workers)(file =>
        readInputStream(IO(new ZstdInputStream(new FileInputStream(file.toNioPath.toFile))), 1024 * 1024)
          .through(fs2.text.utf8.decode)
          .through(fs2.text.lines)
          .filter(_.nonEmpty)
          .evalMapChunk(line =>
            IO(decode[Item](line)).flatMap {
              case Left(err) => IO.raiseError(err)
              case Right(id) => IO.pure(id.id)
            }
          )
          .compile
          .toList
          .map(_.toSet)
          .flatTap(ids => info(s"parsed ${file}: ${ids.size} docs"))
      )
      .compile
      .fold(Set.empty[Int])((a, b) => a ++ b)
    _     <- info(s"${docids.size} docs already scraped")
    queue <- Queue.unbounded[IO, Option[Int]]
    _     <- Stream.range(min, max).filterNot(docids.contains).evalMap(id => queue.offer(Some(id))).compile.drain
    _     <- queue.offer(None)
    size  <- queue.size
    _     <- info(s"added ${size} docs to the queue")
  } yield {
    queue
  }
}
