package com.jaitlapps.kasandra.downloader.http

import java.net.URL

import com.jaitlapps.kasandra.downloader.models.NewsSite.NewsSite
import com.typesafe.scalalogging.StrictLogging

import scalaj.http.Http

class HttpDownloader extends StrictLogging {
  def downloadUrl(url: String, adviceSite: NewsSite): Option[String] = {
    val resp = Http(url).asString

    if (resp.isSuccess && isTrueSite(url, adviceSite)) {
      logger.debug(s"success url: $url")
      Some(resp.body)
    } else if(resp.isRedirect && resp.location.isDefined) {
      val newLocation = resp.location.get
      logger.debug(s"redirect from $url to $newLocation")
      downloadUrl(newLocation, adviceSite)
    } else {
      logger.warn(s"bad url: $url, code: $resp.code, content: $resp.body")
      None
    }
  }

  private def isTrueSite(actual: String, adviceSite: NewsSite): Boolean = {
    val url = new URL(actual).getHost
    url.equals(adviceSite.url)
  }
}
