package com.jaitlapps.kasandra.crawler.db.dao

import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.db.table.RawCrawledPage
import com.jaitlapps.kasandra.crawler.db.table.RawCrawledPagesTable

import scala.concurrent.Future

class RawCrawledPagesDaoSlick(
  override val dbConnection: DbConnection
) extends RawCrawledPagesDao with RawCrawledPagesTable {
  import dbConnection.profile.api._

  private val db = dbConnection.db

  override def save(raw: RawCrawledPage): Future[Int] = db.run {
    rawCrawledPagesQuery += raw
  }
}
