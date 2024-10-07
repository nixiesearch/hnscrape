package ai.nixiesearch.hnscrape

import cats.effect.IO
import org.rogach.scallop.exceptions.{Help, ScallopException, ScallopResult, Version as ScallopVersion}
import org.rogach.scallop.{ScallopConf, ScallopOption, Subcommand, throwError, given}

case class ArgsParser(arguments: List[String]) extends ScallopConf(arguments) with Logging {
  val workers = opt[Int](
    name = "workers",
    required = false,
    default = Some(32),
    descr = "Number of concurrent scraping threads to spawn (optional, default=32)"
  )
  val dir = opt[String](name = "dir", required = true, descr = "An output directory for JSONL dumps")
  val from = opt[Int](
    name = "from",
    required = false,
    default = Some(1),
    descr = "A numeric id to start scraping from (optional, default=1)"
  )
  val to = opt[Int](
    name = "to",
    required = false,
    default = Some(42000000),
    descr = "A numeric id to scrape till, (optional, default=42000000)"
  )
  val batch = opt[Int](
    name = "batch-size",
    required = false,
    default = Some(1024 * 1024),
    descr = "On disk file chunk size, events# (optional, default=1048576)"
  )

  override protected def onError(e: Throwable): Unit = e match {
    case r: ScallopResult if !throwError.value =>
      r match {
        case Help("") =>
          logger.info("\n" + builder.getFullHelpString())
        case Help(subname) =>
          logger.info("\n" + builder.findSubbuilder(subname).get.getFullHelpString())
        case ScallopVersion =>
          "\n" + getVersionString().foreach(logger.info)
        case e @ ScallopException(message) => throw e
        // following should never match, but just in case
        case other: ScallopException => throw other
      }
    case e => throw e
  }
}

object ArgsParser {
  case class Args(workers: Int, dir: String, from: Int, to: Int, batch: Int)
  def parse(args: List[String]): IO[Args] = for {
    parser  <- IO(ArgsParser(args))
    _       <- IO(parser.verify())
    workers <- IO.fromOption(parser.workers.toOption)(new Exception("cannot parse workers"))
    dir     <- IO.fromOption(parser.dir.toOption)(new Exception("cannot parse dir"))
    from    <- IO.fromOption(parser.from.toOption)(new Exception("cannot parse from"))
    to      <- IO.fromOption(parser.to.toOption)(new Exception("cannot parse to"))
    batch   <- IO.fromOption(parser.batch.toOption)(new Exception("cannot parse batch-size"))
  } yield {
    Args(workers, dir, from, to, batch)
  }
}
