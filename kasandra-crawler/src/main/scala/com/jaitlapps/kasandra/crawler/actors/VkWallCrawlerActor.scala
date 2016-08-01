package com.jaitlapps.kasandra.crawler.actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import com.jaitlapps.kasandra.crawler.actors.CrawlControllerActor.CrawledWall
import com.jaitlapps.kasandra.crawler.actors.VkWallCrawlerActor.CrawlVkWall
import com.jaitlapps.kasandra.crawler.crawlers.VkWallCrawler
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.parsers.VkWallParser

import scala.util.{Failure, Success}

class VkWallCrawlerActor extends Actor with ActorLogging {

  private val vkMaxCount = 100

  override def receive: Receive = {
    case CrawlVkWall(site, count) => {
      log.info(s"Crawl site: ${site.domain}")

      var totalUrls = 0
      var offset = 0

      do {
        VkWallCrawler.crawlWall(site.vkGroup, offset, vkMaxCount) match {
          case Success(data) => {
            val urls = VkWallParser.parseJson(data)

            sender ! CrawledWall(urls, site)

            totalUrls += urls.size
            offset += vkMaxCount
            log.info(s"crawled, site: ${site.domain}, offset: $offset, totalUrls: $totalUrls")

            TimeUnit.MILLISECONDS.sleep(1000)
          }

          case Failure(ex) => log.error(ex, s"Error during crawl site: ${site.domain}")
            TimeUnit.MILLISECONDS.sleep(1000)
        }
      } while (totalUrls < count)
    }
  }
}

object VkWallCrawlerActor {
  case class CrawlVkWall(site: CrawlSite, count: Int)

  def props(): Props = Props(new VkWallCrawlerActor)
}