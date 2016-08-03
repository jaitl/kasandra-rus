package com.jaitlapps.kasandra.crawler.actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import com.jaitlapps.kasandra.crawler.actors.SiteCrawlerActor.CrawlUrl
import com.jaitlapps.kasandra.crawler.crawlers.SiteCrawler
import com.jaitlapps.kasandra.crawler.models.{CrawlSite, CrawledVkUrl}
import com.jaitlapps.kasandra.crawler.parsers.ParserFactory

import scala.util.{Failure, Success}

class SiteCrawlerActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case CrawlUrl(url, site) => {
      SiteCrawler.crawl(url, site) match {
        case Success(html) => log.info(s"Crawl url: $url")
          sender ! ParserFactory.getParser(site).parse(html)
          TimeUnit.MILLISECONDS.sleep(1500)
        case Failure(ex) => log.error(ex, s"Error during crawl site: ${site.domain}")
          TimeUnit.MILLISECONDS.sleep(1500)
          self ! CrawlUrl(url, site)
      }
    }
  }
}

object SiteCrawlerActor {
  case class CrawlUrl(url: CrawledVkUrl, site: CrawlSite)

  def props(): Props = Props(new SiteCrawlerActor())
}
