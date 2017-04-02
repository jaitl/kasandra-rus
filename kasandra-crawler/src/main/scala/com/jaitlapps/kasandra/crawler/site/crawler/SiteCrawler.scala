package com.jaitlapps.kasandra.crawler.site.crawler

import java.net.URL

import com.jaitlapps.kasandra.crawler.exceptions.BadUrlException
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.typesafe.scalalogging.StrictLogging

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scalaj.http.Http

object SiteCrawler extends StrictLogging {
  def crawl(url: String, site: CrawlSite): Try[String] = Try {
    implicit val crawlSite: CrawlSite = site

    val resp = Http(url).asString

    if (resp.isSuccess && isTrueSite(url)) {
      resp.body
    } else if(resp.isRedirect && resp.location.isDefined && isTrueSite(url)) {
      val newLocation = resp.location.get

      val newUrl = if (isTrueSite(newLocation)) {
        newLocation
      } else {
        logger.info(s"Recover url: $newLocation")
        recoverUrl(newLocation, url)
      }

      logger.info(s"redirect from $url to $newUrl")

      crawl(newUrl, site) match {
        case Success(html) => html
        case Failure(ex) => throw ex
      }
    } else {
      logger.warn(s"bad url: $url, code: ${resp.code}")
      throw BadUrlException(s"bad url: $url, code: ${resp.code}", resp.code)
    }
  }

  private def isTrueSite(actual: String)(implicit site: CrawlSite): Boolean = {
    try {
      val url = new URL(actual).getHost
      url.contains(site.domain)
    } catch {
      case _: Exception => false
    }
  }

  private def recoverUrl(newUrl: String, oldUrl: String)(implicit site: CrawlSite): String = {
    val oldUrlData = new URL(oldUrl)

    new URL(oldUrlData.getProtocol, oldUrlData.getHost, newUrl).toString
  }
}
