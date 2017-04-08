package com.jaitlapps.kasandra.crawler

import java.util.UUID

import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.site.crawler.SiteCrawler

object OnePageCrawler extends App {
  val url: String = "https://lenta.ru/news/2017/04/03/808/?utm_source=lentavk&utm_medium=social"

  val html = SiteCrawler.crawl(url, CrawlSite(UUID.randomUUID(), SiteType.LentaSite, "lenta.ru", "ria"))

  println(html.get)
}
