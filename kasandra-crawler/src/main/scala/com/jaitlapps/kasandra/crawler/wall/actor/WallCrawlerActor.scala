package com.jaitlapps.kasandra.crawler.wall.actor

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import com.jaitlapps.kasandra.crawler.crawlers.VkWallCrawler
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.parsers.VkWallParser
import com.jaitlapps.kasandra.crawler.utils.RandomUtils

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class WallCrawlerActor(site: CrawlSite, wallDispatcherActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Actor
  with ActorLogging {

  import WallCrawlerActor._

  var groupWallSize: Option[Int] = None
  var totalUrls = 0
  var offset = 0

  var currentRetryCount = 0
  var scheduler: Cancellable = _

  override def receive: Receive = {
    case StartWallCrawl(off) =>
      offset = off
      self ! ScheduleNextPageCrawl

    case CrawlWallPage =>
      log.info(s"Try crawl page, offset: $offset")

      VkWallCrawler.crawlWall(site.vkGroup, offset, vkMaxCount) match {
        case Success(data) =>
          log.info(s"crawled page, offset: $offset")
          self ! ParseCrawlWallPage(data)
          wallDispatcherActor ! WallDispatcherActor.RawCrawlerPage(data, site)

        case Failure(ex) =>
          log.error(ex, s"Error during crawl site: ${site.domain}, offset: $offset, totalUrls: $totalUrls, " +
            s"groupWallSize: $groupWallSize, currentRetry: $currentRetryCount")

          if (currentRetryCount < maxRetry) {
            currentRetryCount += 1
            self ! ScheduleNextPageCrawl
          }
      }

    case ParseCrawlWallPage(html) =>
      Try(VkWallParser.parseJson(html)) match {
        case Success(urls) =>
          wallDispatcherActor ! WallDispatcherActor.CrawledWall(urls, site)

          if (groupWallSize.isEmpty) {
            val wallSize = VkWallParser.parseWallSize(html)
            groupWallSize = Some(wallSize)
          }

          totalUrls += urls.size
          offset += vkMaxCount
          currentRetryCount = 0
          log.info(s"crawled page, site: ${site.domain}, offset: $offset, totalUrls: $totalUrls," +
            s" groupWallSize: $groupWallSize")

          if (totalUrls < groupWallSize.get) {
            self ! ScheduleNextPageCrawl
          } else {
            log.info(s"crawled end, offset: $offset, totalUrls: $totalUrls, groupWallSize: $groupWallSize")
            context.stop(self)
          }

        case Failure(ex) =>
          log.error(ex, s"Error during parse page")
      }

    case ScheduleNextPageCrawl =>
      // TODO: Move to config
      val delay = FiniteDuration(RandomUtils.range(1000, 3000), TimeUnit.MILLISECONDS)
      log.info(s"ScheduleNextPageCrawl, delay: $delay millis")

      scheduler = context.system.scheduler.scheduleOnce(
        delay = delay,
        receiver = self,
        message = CrawlWallPage
      )
  }
}

object WallCrawlerActor {
  // TODO: move to config
  private val vkMaxCount = 100
  private val maxRetry = 10

  case class StartWallCrawl(offset: Int)
  case object CrawlWallPage
  case class ParseCrawlWallPage(html: String)
  case object ScheduleNextPageCrawl

  def props(site: CrawlSite, wallDispatcherActor: ActorRef, executionContext: ExecutionContext): Props =
    Props(new WallCrawlerActor(site, wallDispatcherActor)(executionContext))

  def name(site: CrawlSite): String = s"WallCrawlerActor-${site.siteType.name}"
}
