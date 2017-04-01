package com.jaitlapps.kasandra.crawler.raw.db.table

import java.util.UUID

import com.jaitlapps.kasandra.crawler.db.CustomTypes
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.models.CrawlType
import com.jaitlapps.kasandra.crawler.models.SiteType
import slick.lifted.ProvenShape

case class RawCrawledPage(
  id: UUID,
  siteType: SiteType,
  crawlType: CrawlType,
  url: String,
  content: String,
  isParsed: Boolean
)

trait RawCrawledPagesTable extends CustomTypes {
  val dbConnection: DbConnection

  import dbConnection.profile.api._

  protected class RawCrawledPages(tag: Tag) extends Table[RawCrawledPage](tag, "RawCrawledPages") {
    val id: Rep[UUID] = column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))
    val siteType: Rep[SiteType] = column[SiteType]("siteType")
    val crawlType: Rep[CrawlType] = column[CrawlType]("crawlType")
    val url: Rep[String] = column[String]("url")
    val content: Rep[String] = column[String]("content")
    val isParsed: Rep[Boolean] = column[Boolean]("isParsed")

    override def * : ProvenShape[RawCrawledPage] = (id, siteType, crawlType, url,
      content, isParsed) <> (RawCrawledPage.tupled, RawCrawledPage.unapply)
  }

  protected val rawCrawledPagesQuery: TableQuery[RawCrawledPages] = TableQuery[RawCrawledPages]
}
