package com.jaitlapps.kasandra.crawler.site.db

import com.jaitlapps.kasandra.crawler.site.db.table.CrawledSitePage

import scala.concurrent.Future

trait CrawledSitePagesDao {
  def save(page: CrawledSitePage): Future[Int]
}
