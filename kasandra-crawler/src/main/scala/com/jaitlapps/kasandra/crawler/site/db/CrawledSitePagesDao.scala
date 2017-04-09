package com.jaitlapps.kasandra.crawler.site.db

import java.sql.Timestamp

import com.jaitlapps.kasandra.crawler.site.db.table.CrawledSitePage

import scala.concurrent.Future

trait CrawledSitePagesDao {
  def save(page: CrawledSitePage): Future[Int]
  def list(offset: Int, limit: Int): Future[Seq[CrawledSitePage]]
  def list(offset: Int, limit: Int, start: Timestamp, end: Timestamp): Future[Seq[CrawledSitePage]]
  def count(): Future[Int]
  def count(start: Timestamp, end: Timestamp): Future[Int]
}
