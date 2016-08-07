package com.jaitlapps.kasandra.crawler.actors

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import com.jaitlapps.kasandra.crawler.actors.CrawlControllerActor.CrawledPage
import com.jaitlapps.kasandra.crawler.actors.SiteCrawlerActor.CrawlUrl
import com.jaitlapps.kasandra.crawler.crawlers.SiteCrawler
import com.jaitlapps.kasandra.crawler.exceptions.{BadUrlException, ParseException}
import com.jaitlapps.kasandra.crawler.models.{CrawlSite, CrawledSitePage, CrawledVkUrl}
import com.jaitlapps.kasandra.crawler.parsers.ParserFactory

import scala.util.{Failure, Success}

class SiteCrawlerActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case CrawlUrl(url, site) => {
      SiteCrawler.crawl(url, site) match {
        case Success(html) => log.info(s"Crawl url: $url")
          try {
            val pp = ParserFactory.getParser(site).parse(html)
            sender ! CrawledPage(CrawledSitePage(UUID.randomUUID(), url.date, pp.title, pp.annotation, pp.content, url.url), site)
            TimeUnit.MILLISECONDS.sleep(1500)
          } catch {
            case ParseException() => log.error(s"Parse error, url: $url")
            case e: Exception => log.error(s"Unknown parse error, url: $url")
          }
        case Failure(ex) => ex match {
          case BadUrlException(_) => log.error(ex, s"BadUrlException")
          case _ => log.error(ex, s"Error during crawl site: ${site.domain}")
            TimeUnit.MILLISECONDS.sleep(1500)
            self ! CrawlUrl(url, site)
        }
      }
    }
  }
}

object SiteCrawlerActor {
  case class CrawlUrl(url: CrawledVkUrl, site: CrawlSite)

  def props(): Props = Props(new SiteCrawlerActor())
}
