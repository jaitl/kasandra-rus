package com.jaitlapps.kasandra.crawler.apps

import java.io.PrintWriter
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.json.JsonExtension
import com.jaitlapps.kasandra.crawler.site.db.CrawledSitePagesDaoSlick
import com.jaitlapps.kasandra.crawler.site.db.table.CrawledSitePage
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.json4s.NoTypeHints
import org.json4s.native.Serialization

import scala.concurrent.ExecutionContext
import org.json4s.native.Serialization.write

import scala.util.Failure
import scala.util.Success

object SaveCrawledPages extends App with StrictLogging with JsonExtension {
  implicit val formats = Serialization.formats(NoTypeHints) + UUIDSerialiser + SiteTypeSerialiser + TimestampSerialiser
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(10))
  implicit val system = ActorSystem("KasandraSaveCrawledPagesSystem")
  implicit val materializer = ActorMaterializer()

  val dbConnection = new DbConnection
  val config = ConfigFactory.load()
  val crawledSavePath = config.getString("save.crawled.path")
  val crawledSitePagesDao = new CrawledSitePagesDaoSlick(dbConnection)
  private val dt = new SimpleDateFormat("dd.MM.yyyy_HH-mm-ss")

  val limit: Int = 100

  val countFuture = crawledSitePagesDao.count()
  val fileName = dt.format(new Date)
  val file = new PrintWriter(Paths.get(crawledSavePath, fileName + ".json").toFile)

  val pageFlow = Flow[Int]
    .mapAsync(1) { page =>
      val offset = limit * page
      logger.info(s"page: $page, offset: $offset, limit: $limit")

      crawledSitePagesDao.list(offset, limit)
    }

  val saveFlow = Flow[CrawledSitePage]
    .map { crawledPage =>
      logger.info(s"Save page: ${crawledPage.id}")

      val jsonPage = write(crawledPage)

      file.println(jsonPage)
      file.flush()
    }

  val saveResultFuture = for {
    size <- countFuture
    pages = (Math.ceil(size / limit) + 1).toInt
    range = Range(0, pages)
    parseResult <- Source(range).via(pageFlow).mapConcat(_.toList).via(saveFlow).runWith(Sink.ignore)
  } yield parseResult

  saveResultFuture.onComplete {
    case Success(_) =>
      logger.info("Save success finish")
      file.close()
      system.terminate()
    case Failure(ex) =>
      logger.error("Error during parse", ex)
      file.close()
      system.terminate()
  }
}
