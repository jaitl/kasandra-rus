package com.jaitlapps.kasandra.crawler

import java.util.UUID
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.parser.ParserFactory
import com.jaitlapps.kasandra.crawler.raw.db.RawCrawledPagesDaoSlick
import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPage
import com.jaitlapps.kasandra.crawler.site.db.CrawledSitePagesDaoSlick
import com.jaitlapps.kasandra.crawler.site.db.table.CrawledSitePage
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDaoSlick
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

object ParserApp extends App with StrictLogging {
  implicit val system = ActorSystem("KasandraParseSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(10))

  val dbConnection = new DbConnection
  val rawCrawledPagesDao = new RawCrawledPagesDaoSlick(dbConnection)
  val wallLinksDao = new WallLinksDaoSlick(dbConnection)
  val crawledSitePagesDao = new CrawledSitePagesDaoSlick(dbConnection)

  val sites: Set[SiteType] = SiteType.list
  val limit = 100

  val siteFuture = rawCrawledPagesDao.size(sites)

  val pageFlow = Flow[Int]
    .mapAsync(1) { page =>
      val offset = limit * page
      logger.info(s"page: $page, offset: $offset, limit: $limit")

      rawCrawledPagesDao.crawledPagesWithLink(SiteType.list, offset, limit)
    }

  val parseFlow = Flow[(RawCrawledPage, WallLink)]
    .mapAsync(4) {
      case (raw, link) if !raw.isParsed && !raw.isFailed =>
        Try(ParserFactory.getParser(link.siteType).parse(raw.content)) match {
          case Success(page) =>
            logger.info(s"Parsed page: siteType: ${link.siteType}, id: ${raw.id}, url: ${link.url}")
            val crawledSitePage = CrawledSitePage(
              UUID.randomUUID(), link.timestamp, page.title, page.content, link.url, link.siteType
            )

            for {
              _ <- crawledSitePagesDao.save(crawledSitePage)
              _ <- rawCrawledPagesDao.markAsParsed(raw.id)
            } yield ()
          case Failure(ex) =>
            logger.error("Error during parse", ex)
            rawCrawledPagesDao.markAsFailed(raw.id)
        }
      case (raw, link) =>
        logger.warn(s"Already Parsed page: siteType: ${link.siteType}, id: ${raw.id}, url: ${link.url}")
        Future.successful()
    }

  val parseResultFuture = for {
    size <- siteFuture
    pages = (Math.ceil(size / limit) + 1).toInt
    range = Range(0, pages)
    parseResult <- Source(range).via(pageFlow).mapConcat(_.toList).via(parseFlow).runWith(Sink.ignore)
  } yield parseResult

  parseResultFuture.onComplete {
    case Success(_) =>
      logger.info("Parse success finish")
      system.terminate()
    case Failure(ex) =>
      logger.error("Error during parse", ex)
      system.terminate()
  }
}
