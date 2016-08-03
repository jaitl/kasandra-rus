package com.jaitlapps.kasandra.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.jaitlapps.kasandra.crawler.actors.CrawlControllerActor.{CrawledPage, CrawledWall, StartCrawling}
import com.jaitlapps.kasandra.crawler.actors.FileSaveActor.SavePage
import com.jaitlapps.kasandra.crawler.actors.SiteCrawlerActor.CrawlUrl
import com.jaitlapps.kasandra.crawler.actors.VkWallCrawlerActor.CrawlVkWall
import com.jaitlapps.kasandra.crawler.config.CrawlConfig
import com.jaitlapps.kasandra.crawler.models.{CrawlSite, CrawledVkUrl, CrawledSitePage, SiteType}

class CrawlControllerActor(config: CrawlConfig,
                           vkWallCrawler: ActorRef,
                           siteCrawlers: Map[SiteType, ActorRef],
                           saveActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case StartCrawling => {
      log.info("Start crawling...")
      config.sites.foreach(s => vkWallCrawler ! CrawlVkWall(s, config.count))
    }

    case CrawledWall(urls, site) => {
      log.info(s"Crawled wall, site: ${site.domain}, count: [${urls.size}]")
      val crawler = siteCrawlers(site.siteType)
      urls.foreach(url => crawler ! CrawlUrl(url, site))
    }

    case CrawledPage(page, site) => {
      log.info(s"Crawled wall, site: ${site.domain}, title: [${page.title}]")
      saveActor ! SavePage(page)
    }
  }
}

object CrawlControllerActor {
  case object StartCrawling

  case class CrawledWall(urls: Seq[CrawledVkUrl], site: CrawlSite)
  case class CrawledPage(page: CrawledSitePage, site: CrawlSite)

  def props(config: CrawlConfig,
            vkWallCrawler: ActorRef,
            siteCrawlers: Map[SiteType, ActorRef],
            saveActor: ActorRef): Props =
    Props(new CrawlControllerActor(config, vkWallCrawler, siteCrawlers, saveActor))
}
