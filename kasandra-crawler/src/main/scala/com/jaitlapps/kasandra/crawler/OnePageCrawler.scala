package com.jaitlapps.kasandra.crawler

import java.util.UUID

import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.site.crawler.SiteCrawler

object OnePageCrawler extends App {
  val url: String = "http://www.kp.ru/online/news/2705714/"

  val html = SiteCrawler.crawl(url, CrawlSite(UUID.randomUUID(), SiteType.LentaSite, "kp.ru", "ria"))

  println(html.get)
}
