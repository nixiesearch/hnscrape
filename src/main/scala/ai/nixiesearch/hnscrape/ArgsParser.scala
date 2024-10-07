package ai.nixiesearch.hnscrape

import cats.effect.IO
import org.rogach.scallop.exceptions.{Help, ScallopException, ScallopResult, Version as ScallopVersion}
import org.rogach.scallop.{ScallopConf, ScallopOption, Subcommand, throwError, given}

case class ArgsParser(arguments: List[String]) extends ScallopConf(arguments) with Logging {
  val workers = opt[Int](name = "workers", required = true)
  val dir     = opt[String](name = "dir", required = true)

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
  case class Args(workers: Int, dir: String)
  def parse(args: List[String]): IO[Args] = for {
    parser  <- IO(ArgsParser(args))
    _       <- IO(parser.verify())
    workers <- IO.fromOption(parser.workers.toOption)(new Exception("cannot parse workers"))
    dir     <- IO.fromOption(parser.dir.toOption)(new Exception("cannot parse dir"))
  } yield {
    Args(workers, dir)
  }
}
