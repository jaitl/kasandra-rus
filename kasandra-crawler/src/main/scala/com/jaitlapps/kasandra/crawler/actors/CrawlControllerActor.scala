package com.jaitlapps.kasandra.crawler.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.jaitlapps.kasandra.crawler.actors.CrawlControllerActor.{CrawledWall, StartCrawling}
import com.jaitlapps.kasandra.crawler.actors.VkWallCrawlerActor.CrawlVkWall
import com.jaitlapps.kasandra.crawler.config.CrawlConfig
import com.jaitlapps.kasandra.crawler.models.{CrawlSite, CrawledVkUrl}

class CrawlControllerActor(config: CrawlConfig, vkWallCrawler: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case StartCrawling => {
      log.info("Start crawling...")
      config.sites.foreach(s => vkWallCrawler ! CrawlVkWall(s, config.count))
    }

    case CrawledWall(urls, site) => {
      log.info(s"Crawled wall, site: ${site.domain}, urls: [${urls.mkString(", ")}]")
    }
  }
}

object CrawlControllerActor {
  case object StartCrawling
  case class CrawledWall(urls: Seq[CrawledVkUrl], site: CrawlSite)

  def props(config: CrawlConfig, vkWallCrawler: ActorRef): Props = Props(new CrawlControllerActor(config, vkWallCrawler))
}
