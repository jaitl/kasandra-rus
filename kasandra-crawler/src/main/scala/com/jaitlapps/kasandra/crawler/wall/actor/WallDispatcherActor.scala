package com.jaitlapps.kasandra.crawler.wall.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import com.jaitlapps.kasandra.crawler.actor.ActorCreator
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.wall.db.table.CrawlWall

class WallDispatcherActor(
  wallCrawlerActorCreator: ActorCreator[CrawlSite]
)
  extends Actor
  with ActorLogging {
  import WallDispatcherActor._

  override def receive: Receive = {
    case StartCrawling(sites) =>
      log.info(s"Start crawling..., siteCount: ${sites.size}, sites: [${sites.mkString(", ")}]")

      sites.foreach { site =>
        val crawlSite = CrawlSite(site.id, site.siteType, site.domain, site.vkGroup)

        val vkWallCrawler = wallCrawlerActorCreator.create(context, WallCrawlerActor.name(site))(crawlSite)

        vkWallCrawler ! WallCrawlerActor.StartWallCrawl(site.currentOffset, site.totalWallSize)
        log.info(s"Started wall crawler for: ${site.siteType.name}")
      }
  }
}

object WallDispatcherActor {
  case class StartCrawling(sites: Seq[CrawlWall])

  def props(wallCrawlerActorCreator: ActorCreator[CrawlSite]): Props =
    Props(new WallDispatcherActor(wallCrawlerActorCreator))
  def name(): String = "WallDispatcherActor"
}
