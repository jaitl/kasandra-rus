package com.jaitlapps.kasandra.crawler.raw.db.table

import java.sql.Timestamp
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
  url: Option[String],
  offset: Option[String],
  linkId: Option[UUID],
  content: String,
  crawlTime: Timestamp,
  isParsed: Boolean = false
)

trait RawCrawledPagesTable extends CustomTypes {
  val dbConnection: DbConnection

  import dbConnection.profile.api._

  protected class RawCrawledPages(tag: Tag) extends Table[RawCrawledPage](tag, "RawCrawledPages") {
    val id: Rep[UUID] = column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))
    val siteType: Rep[SiteType] = column[SiteType]("siteType")
    val crawlType: Rep[CrawlType] = column[CrawlType]("crawlType")
    val url: Rep[Option[String]] = column[Option[String]]("url")
    val offset: Rep[Option[String]] = column[Option[String]]("offset")
    val linkId: Rep[Option[UUID]] = column[Option[UUID]]("linkId")
    val content: Rep[String] = column[String]("content")
    val crawlTime: Rep[Timestamp] = column[Timestamp]("crawlTime")
    val isParsed: Rep[Boolean] = column[Boolean]("isParsed")

    override def * : ProvenShape[RawCrawledPage] = (id, siteType, crawlType, url, offset, linkId,
      content, crawlTime, isParsed) <> (RawCrawledPage.tupled, RawCrawledPage.unapply)
  }

  protected val rawCrawledPagesQuery: TableQuery[RawCrawledPages] = TableQuery[RawCrawledPages]
}
