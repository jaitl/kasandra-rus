package com.jaitlapps.kasandra.crawler.wall.actor

import java.sql.Timestamp
import java.util.UUID

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.pattern.pipe
import com.jaitlapps.kasandra.crawler.models.CrawledVkUrl
import com.jaitlapps.kasandra.crawler.wall.db.CrawlWallDao
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDao
import com.jaitlapps.kasandra.crawler.wall.db.table.CrawlWall
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

class WallDispatcherActor(
  wallLinksDao: WallLinksDao,
  crawlWallDao: CrawlWallDao
)(implicit executionContext: ExecutionContext)
  extends Actor
  with ActorLogging {
  import WallDispatcherActor._

  private val config = WallDispatcherConfig(ConfigFactory.load().getConfig("wall.dispatcher"))

  override def receive: Receive = {
    case StartCrawling(sites) =>
      log.info(s"Start crawling..., siteCount: ${sites.size}, sites: [${sites.mkString(", ")}]")

      sites.foreach { site =>
        val vkWallCrawler = context.actorOf(
          props = WallCrawlerActor.props(site, crawlWallDao, self, executionContext),
          name = WallCrawlerActor.name(site)
        )
        vkWallCrawler ! WallCrawlerActor.StartWallCrawl
        log.info(s"Started wall crawler for: ${site.siteType.name}")
      }

    case CrawledWall(urls, site) =>
      log.info(s"Crawled wall, site: ${site.domain}, count: [${urls.size}]")
      val wallLinks = urls.map(c =>
        WallLink(UUID.randomUUID(), new Timestamp(c.date), site.siteType, c.url, isDownloaded = false)).toSeq
      wallLinksDao.saveBatch(wallLinks).pipeTo(self)

    case RawCrawlerPage(page, site) =>
      log.info(s"Crawled page, site: ${site.domain}, page: $page")

    case akka.actor.Status.Failure(ex) =>
      log.error(ex, "Error")
  }
}

object WallDispatcherActor {
  case class StartCrawling(sites: Seq[CrawlWall])
  case class CrawledWall(urls: Set[CrawledVkUrl], site: CrawlWall)
  case class RawCrawlerPage(page: String, site: CrawlWall)

  case class WallDispatcherConfig(rawDataPath: String)
  object WallDispatcherConfig {
    def apply(config: Config): WallDispatcherConfig = WallDispatcherConfig(
      rawDataPath = config.getString("raw-data-path")
    )
  }

  def props(wallLinksDao: WallLinksDao, crawlWallDao: CrawlWallDao, executionContext: ExecutionContext): Props =
    Props(new WallDispatcherActor(wallLinksDao, crawlWallDao)(executionContext))
  def name(): String = "WallDispatcherActor"
}
