package com.jaitlapps.kasandra.crawler.apps

import java.util.UUID

import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.site.crawler.SiteCrawler

object OnePageCrawler extends App {
  val url: String = "http://www.vesti.ru/doc.html?id=2874404&cid=9"

  val html = SiteCrawler.crawl(url, CrawlSite(UUID.randomUUID(), SiteType.VestiSite, "vesti.ru", "ria"))

  println(html.get)
}
