package com.jaitlapps.kasandra.crawler.raw.db

import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPage

import scala.concurrent.Future

trait RawCrawledPagesDao {
  def save(raw: RawCrawledPage): Future[Int]
}
