package com.jaitlapps.kasandra.crawler.article.actor

import akka.actor.Actor
import com.jaitlapps.kasandra.crawler.actor.ActorCreator
import com.jaitlapps.kasandra.crawler.models.CrawlSite

class SiteDispatcherActor(siteCrawlerActorCreator: ActorCreator[CrawlSite]) extends Actor {
  import SiteDispatcherActor._

  override def receive: Receive = {
    case StartSiteDownload(sites) =>
      sites.foreach { site =>
        val actor = siteCrawlerActorCreator.create(context, SiteCrawlerActor.name(site.siteType))(site)
        actor ! SiteCrawlerActor.StartCrawlSite
      }
  }
}

object SiteDispatcherActor {
  case class StartSiteDownload(sites: Seq[CrawlSite])
}
