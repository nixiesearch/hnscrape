package ai.nixiesearch.hnscrape

import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import java.nio.file.Paths

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    args  <- ArgsParser.parse(args)
    queue <- WorkQueue.create(Paths.get(args.dir), args.workers, args.from, args.to)
    _ <- HNAPI
      .create()
      .use(api =>
        queue
          .stream()
          .chunkN(64)
          .unchunks
          .through(PrintProgress.tap("items"))
          .parEvalMapUnordered(args.workers)(id =>
            api.item(id).map(Option.apply).handleErrorWith(e => queue.offer(id).map(_ => None))
          )
          .flatMap(op => Stream.fromOption(op))
          .through(RollingJsonSink.write(Paths.get(args.dir), args.batch))
          .compile
          .drain
      )
  } yield {
    ExitCode.Success
  }
}
