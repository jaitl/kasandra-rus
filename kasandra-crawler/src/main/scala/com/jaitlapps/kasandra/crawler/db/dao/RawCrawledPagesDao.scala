package com.jaitlapps.kasandra.crawler.db.dao

import com.jaitlapps.kasandra.crawler.db.table.RawCrawledPage

import scala.concurrent.Future

trait RawCrawledPagesDao {
  def save(raw: RawCrawledPage): Future[Int]
}
