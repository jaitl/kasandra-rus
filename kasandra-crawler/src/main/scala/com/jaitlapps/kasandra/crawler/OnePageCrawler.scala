package com.jaitlapps.kasandra.crawler

import java.util.UUID

import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.site.crawler.SiteCrawler

object OnePageCrawler extends App {
  val url: String = "http://www.1tv.ru/news/2017-03-26/322271-politsiya_monako_ischet_prestupnikov_ograbivshih_yuvelirnyy_salon_v_tsentre_monte_karlo"

  val html = SiteCrawler.crawl(url, CrawlSite(UUID.randomUUID(), SiteType.RiaSite, "1tv.ru", "ria"))

  println(html.get)
}
