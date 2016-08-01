package com.jaitlapps.kasandra.crawler.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.jaitlapps.kasandra.crawler.actors.VkWallCrawlerActor.CrawlVkWall
import com.jaitlapps.kasandra.crawler.crawlers.VkWallCrawler
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.parsers.VkWallParser

class VkWallCrawlerActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case CrawlVkWall(site, count) => {
      log.info(s"Crawl site: ${site.domain}")

      val data = VkWallCrawler.crawlWall(site.vkGroup, 0, 10)
      println(data)

      val urls = VkWallParser.parseJson(data.get)
    }
  }
}

object VkWallCrawlerActor {
  case class CrawlVkWall(site: CrawlSite, count: Int)

  def props(): Props = Props(new VkWallCrawlerActor)
}