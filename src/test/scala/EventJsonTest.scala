import ai.nixiesearch.hnscrape.Item
import io.circe.Json
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe.parser.*
import io.circe.syntax.*

class EventJsonTest extends AnyFlatSpec with Matchers {

  it should "roundtrip dummy jsons" in {
    val item    = Item(1, Json.obj("id" -> Json.fromInt(1)))
    val string  = item.asJson.noSpaces
    val decoded = decode[Item](string)
    decoded shouldBe Right(item)
  }
  it should "parse story" in {
    val json =
      """{
        |  "by" : "dhouston",
        |  "descendants" : 71,
        |  "id" : 8863,
        |  "kids" : [ 8952, 9224, 8917, 8884, 8887, 8943, 8869, 8958, 9005, 9671, 8940, 9067, 8908, 9055, 8865, 8881, 8872, 8873, 8955, 10403, 8903, 8928, 9125, 8998, 8901, 8902, 8907, 8894, 8878, 8870, 8980, 8934, 8876 ],
        |  "score" : 111,
        |  "time" : 1175714200,
        |  "title" : "My YC app: Dropbox - Throw away your USB drive",
        |  "type" : "story",
        |  "url" : "http://www.getdropbox.com/u/2/screencast.html"
        |}""".stripMargin
    val event = decode[Item](json)
    event shouldBe a[Right[?, ?]]
  }

  it should "parse comment" in {
    val json =
      """{
        |  "by" : "norvig",
        |  "id" : 2921983,
        |  "kids" : [ 2922097, 2922429, 2924562, 2922709, 2922573, 2922140, 2922141 ],
        |  "parent" : 2921506,
        |  "text" : "Aw shucks, guys ... you make me blush with your compliments.<p>Tell you what, Ill make a deal: I'll keep writing if you keep reading. K?",
        |  "time" : 1314211127,
        |  "type" : "comment"
        |}""".stripMargin
    val event = decode[Item](json)
    event shouldBe a[Right[?, ?]]
  }

  it should "parse comments without kids" in {
    val json = """{
                 |  "by" : "perler",
                 |  "descendants" : 0,
                 |  "id" : 5,
                 |  "score" : 7,
                 |  "time" : 1160419864,
                 |  "title" : "Google, YouTube acquisition announcement could come tonight",
                 |  "type" : "story",
                 |  "url" : "http://www.techcrunch.com/2006/10/09/google-youtube-sign-more-separate-deals/"
                 |}""".stripMargin
    val event = decode[Item](json)
    event shouldBe a[Right[?, ?]]
  }

  it should "parse ask" in {
    val json =
      """{
        |  "by" : "tel",
        |  "descendants" : 16,
        |  "id" : 121003,
        |  "kids" : [ 121016, 121109, 121168 ],
        |  "score" : 25,
        |  "text" : "<i>or</i> HN: the Next Iteration<p>I get the impression that with Arc being released a lot of people who never had time for HN before are suddenly dropping in more often. (PG: what are the numbers on this? I'm envisioning a spike.)<p>Not to say that isn't great, but I'm wary of Diggification. Between links comparing programming to sex and a flurry of gratuitous, ostentatious  adjectives in the headlines it's a bit concerning.<p>80% of the stuff that makes the front page is still pretty awesome, but what's in place to keep the signal/noise ratio high? Does the HN model still work as the community scales? What's in store for (++ HN)?",
        |  "time" : 1203647620,
        |  "title" : "Ask HN: The Arc Effect",
        |  "type" : "story"
        |}""".stripMargin
    val event = decode[Item](json)
    event shouldBe a[Right[?, ?]]
  }

  it should "parse job" in {
    val json =
      """{
        |  "by" : "justin",
        |  "id" : 192327,
        |  "score" : 6,
        |  "text" : "Justin.tv is the biggest live video site online. We serve hundreds of thousands of video streams a day, and have supported up to 50k live concurrent viewers. Our site is growing every week, and we just added a 10 gbps line to our colo. Our unique visitors are up 900% since January.<p>There are a lot of pieces that fit together to make Justin.tv work: our video cluster, IRC server, our web app, and our monitoring and search services, to name a few. A lot of our website is dependent on Flash, and we're looking for talented Flash Engineers who know AS2 and AS3 very well who want to be leaders in the development of our Flash.<p>Responsibilities<p><pre><code>    * Contribute to product design and implementation discussions\n    * Implement projects from the idea phase to production\n    * Test and iterate code before and after production release \n</code></pre>\nQualifications<p><pre><code>    * You should know AS2, AS3, and maybe a little be of Flex.\n    * Experience building web applications.\n    * A strong desire to work on website with passionate users and ideas for how to improve it.\n    * Experience hacking video streams, python, Twisted or rails all a plus.\n</code></pre>\nWhile we're growing rapidly, Justin.tv is still a small, technology focused company, built by hackers for hackers. Seven of our ten person team are engineers or designers. We believe in rapid development, and push out new code releases every week. We're based in a beautiful office in the SOMA district of SF, one block from the caltrain station. If you want a fun job hacking on code that will touch a lot of people, JTV is for you.<p>Note: You must be physically present in SF to work for JTV. Completing the technical problem at <a href=\"http://www.justin.tv/problems/bml\" rel=\"nofollow\">http://www.justin.tv/problems/bml</a> will go a long way with us. Cheers!",
        |  "time" : 1210981217,
        |  "title" : "Justin.tv is looking for a Lead Flash Engineer!",
        |  "type" : "job",
        |  "url" : ""
        |}""".stripMargin
    val event = decode[Item](json)
    event shouldBe a[Right[?, ?]]
  }

  it should "parse poll" in {
    val json =
      """{
        |  "by" : "pg",
        |  "descendants" : 54,
        |  "id" : 126809,
        |  "kids" : [ 126822, 126823, 126993, 126824, 126934, 127411, 126888, 127681, 126818, 126816, 126854, 127095, 126861, 127313, 127299, 126859, 126852, 126882, 126832, 127072, 127217, 126889, 127535, 126917, 126875 ],
        |  "parts" : [ 126810, 126811, 126812 ],
        |  "score" : 46,
        |  "text" : "",
        |  "time" : 1204403652,
        |  "title" : "Poll: What would happen if News.YC had explicit support for polls?",
        |  "type" : "poll"
        |}""".stripMargin
    val event = decode[Item](json)
    event shouldBe a[Right[?, ?]]
  }

  it should "parse pollopt" in {
    val json = """{
                 |  "by" : "pg",
                 |  "id" : 160705,
                 |  "poll" : 160704,
                 |  "score" : 335,
                 |  "text" : "Yes, ban them; I'm tired of seeing Valleywag stories on News.YC.",
                 |  "time" : 1207886576,
                 |  "type" : "pollopt"
                 |}""".stripMargin
    val event = decode[Item](json)
    event shouldBe a[Right[?, ?]]
  }

  it should "parse dead events" in {
    val json = """{
                 |  "dead" : true,
                 |  "deleted" : true,
                 |  "id" : 2313,
                 |  "time" : 1173088739,
                 |  "type" : "story"
                 |}""".stripMargin
    val event = decode[Item](json)
    event shouldBe a[Right[?, ?]]
  }
}
