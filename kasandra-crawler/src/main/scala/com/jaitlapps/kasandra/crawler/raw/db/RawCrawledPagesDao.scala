package com.jaitlapps.kasandra.crawler.raw.db

import java.util.UUID

import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPage

import scala.concurrent.Future

trait RawCrawledPagesDao {
  def save(raw: RawCrawledPage): Future[Int]
  def markAsParsed(id: UUID): Future[Int]
}
