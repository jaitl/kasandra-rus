package com.jaitlapps.kasandra.crawler.article.crawler

import java.net.URL

import com.jaitlapps.kasandra.crawler.exceptions.BadUrlException
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.CrawledWallUrl
import com.typesafe.scalalogging.StrictLogging

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scalaj.http.Http

object SiteCrawler extends StrictLogging {
  def crawl(url: String, site: CrawlSite): Try[String] = Try {
    val resp = Http(url).asString

    if (resp.isSuccess && isTrueSite(url, site)) {
      resp.body
    } else if(resp.isRedirect && resp.location.isDefined && isTrueSite(url, site)) {
      val newLocation = resp.location.get
      logger.debug(s"redirect from $url to $newLocation")

      crawl(newLocation, site) match {
        case Success(html) => html
        case Failure(ex) => throw ex
      }
    } else {
      logger.warn(s"bad url: $url, code: ${resp.code}")
      throw BadUrlException(s"bad url: $url, code: ${resp.code}")
    }
  }

  private def isTrueSite(actual: String, site: CrawlSite): Boolean = {
    val url = new URL(actual).getHost
    url.equals(site.domain)
  }
}
