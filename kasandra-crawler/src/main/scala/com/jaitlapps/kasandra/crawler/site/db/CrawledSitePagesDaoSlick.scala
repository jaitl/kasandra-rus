package com.jaitlapps.kasandra.crawler.site.db

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
}
