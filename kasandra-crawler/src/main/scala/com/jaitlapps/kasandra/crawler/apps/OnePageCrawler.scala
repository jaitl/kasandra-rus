package com.jaitlapps.kasandra.crawler.apps

import java.util.UUID

import com.jaitlapps.kasandra.crawler.article.crawler.SiteCrawler
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.SiteType

object OnePageCrawler extends App {
  val url: String = "http://ria.ru/olympics2016/20160806/1473767134.html"

  val html = SiteCrawler.crawl(url, CrawlSite(UUID.randomUUID(), SiteType.RiaSite, "ria.ru", "ria"))

  println(html)
  println("end")
}
