package com.jaitlapps.kasandra.crawler.article.actor

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Cancellable
import akka.actor.Props
import akka.pattern.pipe
import com.jaitlapps.kasandra.crawler.article.actor.SiteCrawlerActor.SiteCrawlerConfig
import com.jaitlapps.kasandra.crawler.article.crawler.SiteCrawler
import com.jaitlapps.kasandra.crawler.article.db.CrawledSitePagesDao
import com.jaitlapps.kasandra.crawler.article.db.table.CrawledSitePage
import com.jaitlapps.kasandra.crawler.article.parser.ParserFactory
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.utils.RandomUtils
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDao
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class SiteCrawlerActor(
  site: CrawlSite,
  config: SiteCrawlerConfig,
  crawledSitePagesDao: CrawledSitePagesDao,
  wallLinksDao: WallLinksDao
)(implicit val executionContext: ExecutionContext) extends Actor with ActorLogging {
  import SiteCrawlerActor._

  var currentRetryCount = 0
  var scheduler: Cancellable = _

  override def receive: Receive = {
    case StartCrawlSite =>
      self ! ScheduleUrlSiteCrawl

    case FindSiteUrl =>
      wallLinksDao.findRandomNotDownloadedLink(site.siteType)
        .map {
          case Some(wallLink) => CrawlUrl(wallLink)
          case None => ScheduleUrlSiteCrawl
        }
        .pipeTo(self)

    case CrawlUrl(wallLink) =>
      SiteCrawler.crawl(wallLink.url, site) match {
        case Success(html) =>
          log.info(s"Crawl url: ${wallLink.url}")
          self ! ParseSitePage(wallLink, html)

        case Failure(ex) =>
          log.error(ex, s"Crawl site page error, url: ${wallLink.url}")
          retry(wallLink)
      }

    case ParseSitePage(wallLink, data) =>
      Try(ParserFactory.getParser(site).parse(data)) match {
        case Success(page) =>
          currentRetryCount = 0

          val crawledSitePage = CrawledSitePage(
            UUID.randomUUID(), wallLink.timestamp, page.title, page.annotation, page.content, wallLink.url
          )

          val pageSaveResultFuture = for {
            _ <- crawledSitePagesDao.save(crawledSitePage)
            _ <- wallLinksDao.markAsDownloaded(wallLink.id)
          } yield ScheduleUrlSiteCrawl

          pageSaveResultFuture.pipeTo(self)

        case Failure(ex) =>
          log.error(ex, s"Parse error, url: ${wallLink.url}")
          retry(wallLink)
      }

    case ScheduleUrlSiteCrawl =>
      val delay = FiniteDuration(RandomUtils.range(config.delayFrom, config.delayTo), TimeUnit.MILLISECONDS)
      log.info(s"ScheduleUrlSiteCrawl, delay: $delay millis")

      scheduler = context.system.scheduler.scheduleOnce(
        delay = delay,
        receiver = self,
        message = FindSiteUrl
      )

    case ScheduleRetryUrlSiteCrawl(wallLink) =>
      val delay = FiniteDuration(RandomUtils.range(config.delayFrom, config.delayTo), TimeUnit.MILLISECONDS)
      log.info(s"ScheduleUrlSiteCrawl, delay: $delay millis")

      scheduler = context.system.scheduler.scheduleOnce(
        delay = delay,
        receiver = self,
        message = CrawlUrl(wallLink)
      )

    case akka.actor.Status.Failure(ex) =>
      log.error(ex, "Error")
      self ! ScheduleUrlSiteCrawl
  }

  def retry(wallLink: WallLink): Unit = {
    if (currentRetryCount < config.maxRetryCount) {
      currentRetryCount += 1
      self ! ScheduleRetryUrlSiteCrawl(wallLink)
      log.info(s"Retry, currentRetryCount: $currentRetryCount")
    } else {
      log.info(s"Stop, currentRetryCount: $currentRetryCount")
      currentRetryCount = 0
      self ! ScheduleUrlSiteCrawl
    }
  }
}

object SiteCrawlerActor {
  case object StartCrawlSite
  case object FindSiteUrl
  case object ScheduleUrlSiteCrawl
  case class ScheduleRetryUrlSiteCrawl(wallLink: WallLink)

  case class CrawlUrl(wallLink: WallLink)
  case class ParseSitePage(wallLink: WallLink, data: String)


  case class SiteCrawlerConfig(delayFrom: Int, delayTo: Int, maxRetryCount: Int)

  def props(
    site: CrawlSite,
    config: SiteCrawlerConfig,
    crawledSitePagesDao: CrawledSitePagesDao,
    wallLinksDao: WallLinksDao,
    executionContext: ExecutionContext
  ): Props = Props(new SiteCrawlerActor(site, config, crawledSitePagesDao, wallLinksDao)(executionContext))


  def name(siteType: SiteType): String = s"SiteCrawlerActor-${siteType.name}"
}
