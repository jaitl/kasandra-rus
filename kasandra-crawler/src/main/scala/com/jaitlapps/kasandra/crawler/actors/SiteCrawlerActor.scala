package com.jaitlapps.kasandra.crawler.actors

import java.net.{MalformedURLException, SocketTimeoutException}
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import com.jaitlapps.kasandra.crawler.actors.SiteCrawlerActor.CrawlUrl
import com.jaitlapps.kasandra.crawler.actors.SiteCrawlerActor.CrawledPage
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
          case BadUrlException(_) => log.error(s"BadUrlException, url: ${url.url}")
          case _: MalformedURLException => log.error(s"MalformedURLException, url: ${url.url}")
          case _: SocketTimeoutException => log.error(ex, s"SocketTimeoutException, url: ${url.url}")
            TimeUnit.MILLISECONDS.sleep(1500)
            self ! CrawlUrl(url, site)
          case _ => log.error(ex, s"Error during crawl url: ${url.url}")
        }
      }
    }
  }
}

object SiteCrawlerActor {
  case class CrawlUrl(url: CrawledVkUrl, site: CrawlSite)
  case class CrawledPage(page: CrawledSitePage, site: CrawlSite)

  def props(): Props = Props(new SiteCrawlerActor())
}
