package com.jaitlapps.kasandra.crawler.site.db.table

import java.sql.Timestamp
import java.util.UUID

import com.jaitlapps.kasandra.crawler.db.CustomTypes
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.models.SiteType
import slick.lifted.ProvenShape

case class CrawledSitePage(id: UUID, date: Timestamp, title: String, content: String, url: String, siteType: SiteType)

trait CrawledSitePagesTable extends CustomTypes {
  val dbConnection: DbConnection

  import dbConnection.profile.api._

  protected class CrawledSitePages(tag: Tag) extends Table[CrawledSitePage](tag, "CrawledSitePages") {
    val id: Rep[UUID] = column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))

    val date: Rep[Timestamp] = column[Timestamp]("date")

    val title: Rep[String] = column[String]("title")

    val content: Rep[String] = column[String]("content")

    val url: Rep[String] = column[String]("url")

    val siteType: Rep[SiteType] = column[SiteType]("siteType")

    override def * : ProvenShape[CrawledSitePage] = (id, date, title, content,
      url, siteType) <> (CrawledSitePage.tupled, CrawledSitePage.unapply)
  }

  protected val crawledSitePagesQuery: TableQuery[CrawledSitePages] = TableQuery[CrawledSitePages]
}
