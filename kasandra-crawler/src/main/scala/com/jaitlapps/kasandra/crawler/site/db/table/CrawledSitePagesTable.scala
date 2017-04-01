package com.jaitlapps.kasandra.crawler.site.db.table

import java.sql.Timestamp
import java.util.UUID

import com.jaitlapps.kasandra.crawler.db.DbConnection
import slick.lifted.ProvenShape

case class CrawledSitePage(id: UUID, date: Timestamp, title: String, annotation: String, content: String, url: String)

trait CrawledSitePagesTable {
  val dbConnection: DbConnection

  import dbConnection.profile.api._

  protected class CrawledSitePages(tag: Tag) extends Table[CrawledSitePage](tag, "CrawledSitePages") {
    val id: Rep[UUID] = column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))

    val date: Rep[Timestamp] = column[Timestamp]("date")

    val title: Rep[String] = column[String]("title")

    val annotation: Rep[String] = column[String]("annotation")

    val content: Rep[String] = column[String]("content")

    val url: Rep[String] = column[String]("url")

    override def * : ProvenShape[CrawledSitePage] = (id, date, title, annotation, content,
      url) <> (CrawledSitePage.tupled, CrawledSitePage.unapply)
  }

  protected val crawledSitePagesQuery: TableQuery[CrawledSitePages] = TableQuery[CrawledSitePages]
}
