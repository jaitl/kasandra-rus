package com.jaitlapps.kasandra.crawler.wall.actor

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import akka.pattern.pipe
import com.jaitlapps.kasandra.crawler.crawlers.VkWallCrawler
import com.jaitlapps.kasandra.crawler.parsers.VkWallParser
import com.jaitlapps.kasandra.crawler.utils.RandomUtils
import com.jaitlapps.kasandra.crawler.wall.db.CrawlWallDao
import com.jaitlapps.kasandra.crawler.wall.db.table.CrawlWall
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class WallCrawlerActor(
  wall: CrawlWall,
  crawlWallDao: CrawlWallDao,
  wallDispatcherActor: ActorRef
)(implicit executionContext: ExecutionContext)
  extends Actor
  with ActorLogging {

  import WallCrawlerActor._

  private val config: WallCrawlerConfig = WallCrawlerConfig(ConfigFactory.load().getConfig("wall.crawler"))

  var totalUrls = 0
  var offset = 0

  var currentRetryCount = 0
  var scheduler: Cancellable = _

  override def receive: Receive = {
    case StartWallCrawl =>
      self ! ScheduleNextPageCrawl

    case CrawlWallPage =>
      log.info(s"Try crawl page, offset: $offset")

      VkWallCrawler.crawlWall(wall.vkGroup, offset, vkMaxCount) match {
        case Success(data) =>
          log.info(s"crawled page, offset: $offset")
          self ! ParseCrawlWallPage(data)

        case Failure(ex) =>
          log.error(ex, s"Error during crawl site: ${wall.domain}, offset: $offset, totalUrls: $totalUrls, " +
            s"currentRetry: $currentRetryCount")
          retry()
      }

    case ParseCrawlWallPage(html) =>
      Try(VkWallParser.parseJson(html)) match {
        case Success(urls) =>
          val siteUrls = urls.filter(_.url.contains(wall.domain))
          wallDispatcherActor ! WallDispatcherActor.CrawledWall(siteUrls, wall)

          currentRetryCount = 0
          totalUrls += siteUrls.size
          log.info(s"crawled page, site: ${wall.domain}, totalUrls: $totalUrls")

          val newOffset = offset + vkMaxCount
          crawlWallDao.updateOffset(wall.id, newOffset)
            .map(_ => UpdateOffset(newOffset))
            .pipeTo(self)

        case Failure(ex) =>
          log.error(ex, s"Error during parse page")
          retry()
      }

    case UpdateOffset(off) =>
      log.info(s"UpdateOffset, current offset: $off")

      offset = off
      if (offset < wall.totalWallSize) {
        self ! ScheduleNextPageCrawl
      } else {
        log.info(s"crawled end, offset: $offset, totalUrls: $totalUrls, totalWallSize: ${wall.totalWallSize}")
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

  case object StartWallCrawl
  case object CrawlWallPage
  case class ParseCrawlWallPage(html: String)
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
    site: CrawlWall,
    crawlWallDao: CrawlWallDao,
    wallDispatcherActor: ActorRef,
    executionContext: ExecutionContext
  ): Props =
    Props(new WallCrawlerActor(site, crawlWallDao, wallDispatcherActor)(executionContext))

  def name(site: CrawlWall): String = s"WallCrawlerActor-${site.siteType.name}"
}
