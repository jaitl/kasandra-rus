package com.jaitlapps.kasandra.crawler.wall.actor

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Cancellable
import akka.actor.Props
import akka.pattern.pipe
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.CrawlType
import com.jaitlapps.kasandra.crawler.raw.db.RawCrawledPagesDao
import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPage
import com.jaitlapps.kasandra.crawler.utils.RandomUtils
import com.jaitlapps.kasandra.crawler.wall.actor.WallCrawlerActor.WallCrawlerConfig
import com.jaitlapps.kasandra.crawler.wall.crawler.WallCrawler
import com.jaitlapps.kasandra.crawler.wall.db.CrawlWallDao
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDao
import com.jaitlapps.kasandra.crawler.wall.db.table.CrawlWall
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink
import com.jaitlapps.kasandra.crawler.wall.parser.WallParser
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class WallCrawlerActor(
  site: CrawlSite,
  crawlWallDao: CrawlWallDao,
  wallLinksDao: WallLinksDao,
  rawCrawledPagesDao: RawCrawledPagesDao,
  config: WallCrawlerConfig
)(implicit executionContext: ExecutionContext)
  extends Actor
  with ActorLogging {

  import WallCrawlerActor._

  var totalUrls = 0
  var offset = 0
  var totalWallSize = 0

  var currentRetryCount = 0
  var scheduler: Cancellable = _

  override def receive: Receive = {
    case StartWallCrawl(currentOffset, totalSize) =>
      offset = currentOffset
      totalWallSize = totalSize
      self ! ScheduleNextPageCrawl

    case CrawlWallPage =>
      log.info(s"Try crawl page, offset: $offset")

      WallCrawler.crawlWall(site.vkGroup, offset, vkMaxCount) match {
        case Success(data) =>
          log.info(s"crawled page, offset: $offset")
          val rawId = UUID.randomUUID()
          val raw = RawCrawledPage(rawId, site.siteType, CrawlType.Wall, offset.toString, data, isParsed = false)

          rawCrawledPagesDao.save(raw)
            .map(_ => ParseCrawlWallPage(data, rawId))
            .pipeTo(self)

        case Failure(ex) =>
          log.error(ex, s"Error during crawl site: ${site.domain}, offset: $offset, totalUrls: $totalUrls, " +
            s"currentRetry: $currentRetryCount")
          retry()
      }

    case ParseCrawlWallPage(html, rawId) =>
      Try(WallParser.parseJson(html)) match {
        case Success(urls) =>
          val siteUrls = urls.filter(_.url.contains(site.domain))

          currentRetryCount = 0
          totalUrls += siteUrls.size
          log.info(s"crawled page, site: ${site.domain}, totalUrls: $totalUrls")

          val newOffset = offset + vkMaxCount

          val wallLinks = siteUrls.map(c =>
            WallLink(UUID.randomUUID(), c.date, site.siteType, c.url, isDownloaded = false)).toSeq

          val saveResultFuture = for {
            _ <- wallLinksDao.saveBatch(wallLinks)
            _ <- crawlWallDao.updateOffset(site.id, newOffset)
            _ <- rawCrawledPagesDao.markAsParsed(rawId)
          } yield UpdateOffset(newOffset)

          saveResultFuture.pipeTo(self)

        case Failure(ex) =>
          log.error(ex, s"Error during parse page")
          retry()
      }

    case UpdateOffset(off) =>
      log.info(s"UpdateOffset, current offset: $off")

      offset = off
      if (offset < totalWallSize) {
        self ! ScheduleNextPageCrawl
      } else {
        log.info(s"crawled end, offset: $offset, totalUrls: $totalUrls, totalWallSize: $totalWallSize")
        context.stop(self)
      }

    case ScheduleNextPageCrawl =>
      val delay = FiniteDuration(RandomUtils.range(config.delayFrom, config.delayTo), TimeUnit.MILLISECONDS)
      log.info(s"ScheduleNextPageCrawl, delay: $delay millis")

      scheduler = context.system.scheduler.scheduleOnce(
        delay = delay,
        receiver = self,
        message = CrawlWallPage
      )

    case akka.actor.Status.Failure(ex) =>
      log.error(ex, "Error")
      retry()
  }

  def retry(): Unit = {
    if (currentRetryCount < config.maxRetryCount) {
      currentRetryCount += 1
      self ! ScheduleNextPageCrawl
      log.info(s"Retry, currentRetryCount: $currentRetryCount")
    } else {
      log.info(s"Stop, currentRetryCount: $currentRetryCount")
      context.stop(self)
    }
  }
}

object WallCrawlerActor {
  private val vkMaxCount = 100

  case class StartWallCrawl(currentOffset: Int, totalSize: Int)
  case object CrawlWallPage
  case class ParseCrawlWallPage(html: String, rawId: UUID)
  case class UpdateOffset(offset: Int)
  case object ScheduleNextPageCrawl

  case class WallCrawlerConfig(delayFrom: Int, delayTo: Int, maxRetryCount: Int)

  object WallCrawlerConfig {
    def apply(config: Config): WallCrawlerConfig = WallCrawlerConfig(
      delayFrom = config.getInt("delayFrom"),
      delayTo = config.getInt("delayTo"),
      maxRetryCount = config.getInt("maxRetryCount")
    )
  }

  def props(
    site: CrawlSite,
    crawlWallDao: CrawlWallDao,
    wallLinksDao: WallLinksDao,
    rawCrawledPagesDao: RawCrawledPagesDao,
    config: WallCrawlerConfig,
    executionContext: ExecutionContext
  ): Props = Props(
    new WallCrawlerActor(site, crawlWallDao, wallLinksDao, rawCrawledPagesDao, config)(executionContext)
  )

  def name(site: CrawlWall): String = s"WallCrawlerActor-${site.siteType.name}"
}
