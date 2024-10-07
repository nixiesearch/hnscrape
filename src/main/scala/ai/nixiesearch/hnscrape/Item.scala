package ai.nixiesearch.hnscrape

import io.circe.{Decoder, DecodingFailure, Encoder, Json}

case class Item(id: Int, json: Json)

object Item {
  given itemDecoder: Decoder[Item] = Decoder.instance(c =>
    c.downField("id").as[Int] match
      case Left(value)  => Left(DecodingFailure(s"id field is missing", c.history))
      case Right(value) => Right(Item(value, c.value))
  )
  given itemEncoder: Encoder[Item] = Encoder.instance(a => a.json)
  
  
}
