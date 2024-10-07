import ai.nixiesearch.hnscrape.ArgsParser
import ai.nixiesearch.hnscrape.ArgsParser.Args
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.unsafe.implicits.global

class ArgsParserTest extends AnyFlatSpec with Matchers {
  it should "parse args" in {
    val args = ArgsParser.parse(List("--workers", "1", "--dir", "foo")).unsafeRunSync()
    args shouldBe Args(1, "foo")
  }
}
