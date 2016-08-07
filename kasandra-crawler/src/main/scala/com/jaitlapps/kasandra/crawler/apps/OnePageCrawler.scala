package com.jaitlapps.kasandra.crawler.apps

import com.jaitlapps.kasandra.crawler.crawlers.SiteCrawler
import com.jaitlapps.kasandra.crawler.models.{CrawlSite, CrawledVkUrl, SiteType}

object OnePageCrawler extends App {
  val url: String = "http://ria.ru/olympics2016/20160806/1473767134.html"

  val html = SiteCrawler.crawl(CrawledVkUrl(url, 0), CrawlSite(SiteType.RiaSite, "ria.ru", "ria"))

  println(html)
  println("end")
}
