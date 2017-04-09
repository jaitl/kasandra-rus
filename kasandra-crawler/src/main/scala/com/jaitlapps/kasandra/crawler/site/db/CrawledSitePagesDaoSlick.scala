package com.jaitlapps.kasandra.crawler.site.db

import java.sql.Timestamp

import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.site.db.table.CrawledSitePage
import com.jaitlapps.kasandra.crawler.site.db.table.CrawledSitePagesTable

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class CrawledSitePagesDaoSlick(
  override val dbConnection: DbConnection
)(implicit executionContext: ExecutionContext) extends CrawledSitePagesDao with CrawledSitePagesTable {
  import dbConnection.profile.api._

  private val db = dbConnection.db

  override def save(page: CrawledSitePage): Future[Int] = db.run {
    crawledSitePagesQuery += page
  }

  override def list(offset: Int, limit: Int): Future[Seq[CrawledSitePage]] = db.run {
    crawledSitePagesQuery
      .sortBy(_.date asc)
      .drop(offset)
      .take(limit)
      .result
  }

  def list(offset: Int, limit: Int, start: Timestamp, end: Timestamp): Future[Seq[CrawledSitePage]] = db.run {
    crawledSitePagesQuery
      .sortBy(_.date asc)
      .filter(_.date > start)
      .filter(_.date < end)
      .drop(offset)
      .take(limit)
      .result
  }


  override def count(): Future[Int] = db.run {
    crawledSitePagesQuery.size.result
  }

  override def count(start: Timestamp, end: Timestamp): Future[Int] = db.run {
    crawledSitePagesQuery
      .filter(_.date > start)
      .filter(_.date < end)
      .size
      .result
  }
}
