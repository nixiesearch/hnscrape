package ai.nixiesearch.hnscrape

import cats.effect.IO
import cats.effect.kernel.Resource
import org.http4s.{EntityDecoder, Uri}
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.circe.*
import scala.concurrent.duration.*

case class HNAPI(client: Client[IO], endpoint: Uri) {
  given eventJson: EntityDecoder[IO, Item] = jsonOf

  def item(id: Int): IO[Item] =
    client.expect[Item](endpoint.addPath(s"/item/$id.json"))

  def maxitem(): IO[Int] =
    client
      .expect[String](endpoint.addPath("/maxitem.json"))
      .flatMap(str =>
        str.toIntOption match
          case Some(value) => IO.pure(value)
          case None        => IO.raiseError(new Exception(s"$str is not int"))
      )
}

object HNAPI extends Logging {

  val ENDPOINT = "https://hacker-news.firebaseio.com/v0"
  def create(): Resource[IO, HNAPI] = for {
    endpoint <- Resource.eval(IO.fromEither(Uri.fromString(ENDPOINT)))
    client   <- EmberClientBuilder.default[IO].withTimeout(1.second).build
  } yield {
    HNAPI(client, endpoint)
  }
}
