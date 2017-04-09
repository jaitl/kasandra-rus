package com.jaitlapps.kasandra.crawler.apps

import java.io.PrintWriter
import java.nio.file.Paths
import java.util.UUID
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.json.JsonExtension
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.raw.db.RawCrawledPagesDaoSlick
import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPage
import com.jaitlapps.kasandra.crawler.site.db.CrawledSitePagesDaoSlick
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDaoSlick
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

object SaveRawPages extends App with StrictLogging with JsonExtension {
  implicit val formats = Serialization.formats(NoTypeHints) + UUIDSerialiser + SiteTypeSerialiser + TimestampSerialiser

  implicit val system = ActorSystem("KasandraSaveRawPageSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(10))

  val dbConnection = new DbConnection
  val rawCrawledPagesDao = new RawCrawledPagesDaoSlick(dbConnection)
  val wallLinksDao = new WallLinksDaoSlick(dbConnection)
  val crawledSitePagesDao = new CrawledSitePagesDaoSlick(dbConnection)
  val config = ConfigFactory.load()
  val rawSavePath = config.getString("save.raw.path")

  val sites: Set[SiteType] = SiteType.list
  val limit = 100

  val siteFuture = rawCrawledPagesDao.size(sites)

  val pageFlow = Flow[Int]
    .mapAsync(1) { page =>
      val offset = limit * page
      logger.info(s"page: $page, offset: $offset, limit: $limit")

      rawCrawledPagesDao.crawledPagesWithLink(SiteType.list, offset, limit)
    }

  val saveFlow = Flow[(RawCrawledPage, WallLink)].mapAsync(4) {
    case (raw, link) =>
      Future {
        logger.info(s"Save raw page: siteType: ${link.siteType}, id: ${raw.id}, url: ${link.url}")

        val page = SavedRawPage(raw.id, raw.crawlTime.toInstant.toEpochMilli, raw.content, link.url, raw.siteType)
        val pageJson = write(page)

        val file = new PrintWriter(Paths.get(rawSavePath, raw.id.toString + ".json").toFile)

        file.println(pageJson)
        file.flush()
        file.close()
      }
  }

  val saveResultFuture = for {
    size <- siteFuture
    pages = (Math.ceil(size / limit) + 1).toInt
    range = Range(0, pages)
    parseResult <- Source(range).via(pageFlow).mapConcat(_.toList).via(saveFlow).runWith(Sink.ignore)
  } yield parseResult

  saveResultFuture.onComplete {
    case Success(_) =>
      logger.info("Save raw success finish")
      system.terminate()
    case Failure(ex) =>
      logger.error("Error during save raw", ex)
      system.terminate()
  }
}

case class SavedRawPage(id: UUID, date: Long, html: String, url: String, siteType: SiteType)
