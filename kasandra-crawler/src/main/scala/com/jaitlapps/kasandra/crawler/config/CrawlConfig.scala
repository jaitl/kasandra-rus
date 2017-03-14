package com.jaitlapps.kasandra.crawler.config

import com.jaitlapps.kasandra.crawler.models.{CrawlSite, SiteType}
import com.typesafe.config.Config

import scala.collection.JavaConverters._

case class CrawlConfig(sites: Seq[CrawlSite], count: Int)

object CrawlConfig {
  def apply(conf: Config): CrawlConfig = new CrawlConfig(conf.getConfigList("sites")
    .asScala.map(c => CrawlSite(SiteType(c.getString("name")), c.getString("domain"), c.getString("vkGroup"))),
      conf.getInt("count"))
}
