package ai.nixiesearch.hnscrape

import cats.effect.{IO, Ref}
import com.github.luben.zstd.ZstdOutputStream
import fs2.{Chunk, Pipe, Pull, Stream}
import fs2.io.writeOutputStream
import io.circe.Encoder
import io.circe.syntax.*

import java.io.{BufferedOutputStream, FileOutputStream, OutputStream}
import java.nio.file.Path

object RollingJsonSink extends Logging {
  private def flushBuffer[T: Encoder](dir: Path, buffer: List[T]): Unit = {
    val stream = new BufferedOutputStream(
      new ZstdOutputStream(
        new FileOutputStream(dir.resolve(s"items_${System.currentTimeMillis()}.jsonl.zst").toFile)
      )
    )
    buffer.foreach(item => stream.write((item.asJson.noSpaces + "\n").getBytes()))
    stream.close()
  }

  private def writeUncons[T: Encoder](
      stream: Stream[IO, T],
      dir: Path,
      batchSize: Int,
      buffer: List[T]
  ): Pull[IO, T, Unit] =
    stream.pull.uncons.flatMap {
      case None if buffer.isEmpty => Pull.done
      case None =>
        flushBuffer(dir, buffer)
        Pull.done
      case Some((head, next)) =>
        val mergedBuffer = buffer ++ head.toList
        if (mergedBuffer.length >= batchSize) {
          val (flushChunk, remainChunk) = mergedBuffer.splitAt(batchSize)
          flushBuffer(dir, flushChunk)
          writeUncons(next, dir, batchSize, remainChunk)
        } else {
          writeUncons(next, dir, batchSize, mergedBuffer)
        }
    }
  def write[T: Encoder](dir: Path, batchSize: Int): Pipe[IO, T, Unit] = { in =>
    writeUncons(in, dir, batchSize, Nil).stream.drain
  }

  case class Output(name: String, from: Int, to: Int, stream: OutputStream) {
    def close() = IO(stream.close())
  }
  object Output {
    def create(id: Int, dir: Path): IO[Output] = IO {
      val to   = 10000 * math.ceil(id / 10000.0).toInt
      val from = 10000 * math.floor(id / 10000.0).toInt
      val name = s"docs_${from}_${to}.jsonl.zst"
      new Output(
        name = name,
        from = from,
        to = to,
        stream = new BufferedOutputStream(
          new ZstdOutputStream(new FileOutputStream(dir.resolve(name).toFile))
        )
      )
    }
  }
}
