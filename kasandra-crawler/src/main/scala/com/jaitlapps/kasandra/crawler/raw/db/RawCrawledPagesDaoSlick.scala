package com.jaitlapps.kasandra.crawler.raw.db

import java.util.UUID

import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPage
import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPagesTable

import scala.concurrent.Future

class RawCrawledPagesDaoSlick(
  override val dbConnection: DbConnection
) extends RawCrawledPagesDao with RawCrawledPagesTable {
  import dbConnection.profile.api._

  private val db = dbConnection.db

  override def save(raw: RawCrawledPage): Future[Int] = db.run {
    rawCrawledPagesQuery += raw
  }

  override def markAsParsed(id: UUID): Future[Int] = db.run {
    rawCrawledPagesQuery
      .filter(_.id === id)
      .map(_.isParsed)
      .update(true)
  }
}
