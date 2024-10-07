import ai.nixiesearch.hnscrape.{HNAPI, Item}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.unsafe.implicits.global
import io.circe.Json

class HNAPITest extends AnyFlatSpec with Matchers {
  it should "get max id" in {
    val (client, shutdown) = HNAPI.create().allocated.unsafeRunSync()
    val maxid              = client.maxitem().unsafeRunSync()
    maxid should be > 40000000
    shutdown.unsafeRunSync()
  }
  it should "get an item" in {
    val (client, shutdown) = HNAPI.create().allocated.unsafeRunSync()
    val event              = client.item(8863).unsafeRunSync()
    event.id shouldBe 8863
    shutdown.unsafeRunSync()
  }
}
