package com.jaitlapps.kasandra.crawler.wall.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import com.jaitlapps.kasandra.crawler.wall.db.CrawlWallDao
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDao
import com.jaitlapps.kasandra.crawler.wall.db.table.CrawlWall

import scala.concurrent.ExecutionContext

class WallDispatcherActor(
  wallLinksDao: WallLinksDao,
  crawlWallDao: CrawlWallDao
)(implicit executionContext: ExecutionContext)
  extends Actor
  with ActorLogging {
  import WallDispatcherActor._

  override def receive: Receive = {
    case StartCrawling(sites) =>
      log.info(s"Start crawling..., siteCount: ${sites.size}, sites: [${sites.mkString(", ")}]")

      sites.foreach { site =>
        val vkWallCrawler = context.actorOf(
          props = WallCrawlerActor.props(site, crawlWallDao, wallLinksDao, self, executionContext),
          name = WallCrawlerActor.name(site)
        )
        vkWallCrawler ! WallCrawlerActor.StartWallCrawl
        log.info(s"Started wall crawler for: ${site.siteType.name}")
      }

    case akka.actor.Status.Failure(ex) =>
      log.error(ex, "Error")
  }
}

object WallDispatcherActor {
  case class StartCrawling(sites: Seq[CrawlWall])

  def props(wallLinksDao: WallLinksDao, crawlWallDao: CrawlWallDao, executionContext: ExecutionContext): Props =
    Props(new WallDispatcherActor(wallLinksDao, crawlWallDao)(executionContext))
  def name(): String = "WallDispatcherActor"
}
