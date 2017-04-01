package com.jaitlapps.kasandra.crawler.site.actor

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Cancellable
import akka.actor.Props
import akka.pattern.pipe
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.CrawlType
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.raw.db.RawCrawledPagesDao
import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPage
import com.jaitlapps.kasandra.crawler.site.actor.SiteCrawlerActor.SiteCrawlerConfig
import com.jaitlapps.kasandra.crawler.site.crawler.SiteCrawler
import com.jaitlapps.kasandra.crawler.site.db.CrawledSitePagesDao
import com.jaitlapps.kasandra.crawler.utils.RandomUtils
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDao
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success

class SiteCrawlerActor(
  site: CrawlSite,
  config: SiteCrawlerConfig,
  crawledSitePagesDao: CrawledSitePagesDao,
  wallLinksDao: WallLinksDao,
  rawCrawledPagesDao: RawCrawledPagesDao
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
          val raw = RawCrawledPage(
            UUID.randomUUID(), site.siteType, CrawlType.Site, wallLink.url, html, isParsed = false
          )

          val saveResultFuture = for {
            _ <- rawCrawledPagesDao.save(raw)
            _ <- wallLinksDao.markAsDownloaded(wallLink.id)
          } yield ScheduleUrlSiteCrawl

          saveResultFuture.pipeTo(self)

        case Failure(ex) =>
          log.error(ex, s"Crawl site page error, url: ${wallLink.url}")
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

  case class SiteCrawlerConfig(delayFrom: Int, delayTo: Int, maxRetryCount: Int)

  object SiteCrawlerConfig {
    def apply(config: Config): SiteCrawlerConfig = SiteCrawlerConfig(
      delayFrom = config.getInt("delayFrom"),
      delayTo = config.getInt("delayTo"),
      maxRetryCount = config.getInt("maxRetryCount")
    )
  }

  def props(
    site: CrawlSite,
    config: SiteCrawlerConfig,
    crawledSitePagesDao: CrawledSitePagesDao,
    wallLinksDao: WallLinksDao,
    rawCrawledPagesDao: RawCrawledPagesDao,
    executionContext: ExecutionContext
  ): Props =
    Props(new SiteCrawlerActor(site, config, crawledSitePagesDao, wallLinksDao, rawCrawledPagesDao)(executionContext))

  def name(siteType: SiteType): String = s"SiteCrawlerActor-${siteType.name}"
}
