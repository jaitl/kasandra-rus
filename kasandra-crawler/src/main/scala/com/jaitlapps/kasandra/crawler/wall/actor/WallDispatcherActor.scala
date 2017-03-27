package com.jaitlapps.kasandra.crawler.wall.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.CrawledVkUrl

import scala.concurrent.ExecutionContext

class WallDispatcherActor(implicit executionContext: ExecutionContext) extends Actor with ActorLogging {
  import WallDispatcherActor._

  override def receive: Receive = {
    case StartCrawling(sites) =>
      log.info(s"Start crawling..., siteCount: ${sites.size}, sites: [${sites.mkString(", ")}]")

      sites.foreach { site =>
        val vkWallCrawler = context.actorOf(
          props = WallCrawlerActor.props(site, self, executionContext),
          name = WallCrawlerActor.name(site)
        )
        vkWallCrawler ! WallCrawlerActor.StartWallCrawl(0)
        log.info(s"Started wall crawler for: ${site.siteType.name}")
      }

    case CrawledWall(urls, site) =>
      log.info(s"Crawled wall, site: ${site.domain}, count: [${urls.size}], urls: [${urls.mkString(", ")}]")

    case RawCrawlerPage(page, site) =>
      log.info(s"Crawled page, site: ${site.domain}, page: $page")
  }
}

object WallDispatcherActor {
  case class StartCrawling(sites: Seq[CrawlSite])
  case class CrawledWall(urls: Set[CrawledVkUrl], site: CrawlSite)
  case class RawCrawlerPage(page: String, site: CrawlSite)

  def props(executionContext: ExecutionContext): Props = Props(new WallDispatcherActor()(executionContext))
}
