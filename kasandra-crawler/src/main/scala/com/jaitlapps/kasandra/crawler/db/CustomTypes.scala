package com.jaitlapps.kasandra.crawler.db

import com.jaitlapps.kasandra.crawler.models.CrawlType
import com.jaitlapps.kasandra.crawler.models.SiteType

trait CustomTypes {
  val dbConnection: DbConnection

  import dbConnection.profile.api._

  implicit val siteTypeType = MappedColumnType.base[SiteType, String](_.name, SiteType.apply)
  implicit val crawlTypeType = MappedColumnType.base[CrawlType, String](_.name, CrawlType.apply)
}
