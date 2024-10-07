# A simple multi-threaded HN API scraper

A concurrent parse to scrape [HN API](https://github.com/HackerNews/API).

## Features

* Heavily concurrent: written using [Scala Fs2 Streams](https://fs2.io)
* Can resume after failure: scans the output dir for already fetched items on start.
* Error handling and timeouts supported: failed tasks are rescheduled into the queue.

We run this scraper regularly and upload dumps to the Huggingface dataset repo: [TODO]

## Usage

The `hnscrape` cli tool supports the following arguments:

```
INFO ai.nixiesearch.hnscrape.ArgsParser -- 
  -b, --batch-size  <arg>   On disk file chunk size, events# (optional,
                            default=1048576)
  -d, --dir  <arg>          An output directory for JSONL dumps
  -f, --from  <arg>         A numeric id to start scraping from (optional,
                            default=1)
  -t, --to  <arg>           A numeric id to scrape till, (optional,
                            default=42000000)
  -w, --workers  <arg>      Number of concurrent scraping threads to spawn
                            (optional, default=32)
  -h, --help                Show help message
```

Get the `hnscrape.jar` from the releases page, and then start it with:
```shell
java -jar hnscrape.jar --dir /tmp/out 
```

and it will start scraping.

## Output format

`hnscrape` stores raw API responses without any processing:
```json
{
  "by": "pg",
  "descendants": 0,
  "id": 16,
  "score": 11,
  "time": 1160423503,
  "title": "Feld: Question Regarding NDAs",
  "type": "story",
  "url": "http://www.feld.com/blog/archives/001979.html"
}
```

See official [HN API spec](https://github.com/HackerNews/API) for more details on the JSON format.

## Speed and limits

HN API has no concurrency and rate limits, so with 64 concurrent threads it's possible to reach the scraping speed of 500 items/sec. With this speed it's possible to download the whole id space 1..42000000 within 24 hours.

## License

Apache 2.0