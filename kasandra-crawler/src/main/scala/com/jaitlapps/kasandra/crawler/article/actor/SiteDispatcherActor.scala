package com.jaitlapps.kasandra.crawler.article.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import com.jaitlapps.kasandra.crawler.actor.ActorCreator
import com.jaitlapps.kasandra.crawler.models.CrawlSite

class SiteDispatcherActor(siteCrawlerActorCreator: ActorCreator[CrawlSite]) extends Actor with ActorLogging {
  import SiteDispatcherActor._

  override def receive: Receive = {
    case StartSiteCrawl(sites) =>
      log.info(s"StartSiteCrawl, site count: ${sites.size}")

      sites.foreach { site =>
        val actor = siteCrawlerActorCreator.create(context, SiteCrawlerActor.name(site.siteType))(site)
        actor ! SiteCrawlerActor.StartCrawlSite
      }
  }
}

object SiteDispatcherActor {
  case class StartSiteCrawl(sites: Seq[CrawlSite])

  def props(siteCrawlerActorCreator: ActorCreator[CrawlSite]): Props =
    Props(new SiteDispatcherActor(siteCrawlerActorCreator))
}
