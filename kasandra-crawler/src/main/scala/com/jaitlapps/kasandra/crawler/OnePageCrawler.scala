package com.jaitlapps.kasandra.crawler

import java.util.UUID

import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.site.crawler.SiteCrawler

object OnePageCrawler extends App {
  val url: String = "https://russian.rt.com/world/article/374171-wikileaks-hakery-ssha-cru"

  val html = SiteCrawler.crawl(url, CrawlSite(UUID.randomUUID(), SiteType.RiaSite, "rt.com", "ria"))

  println(html.get)
}
