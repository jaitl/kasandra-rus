package com.jaitlapps.kasandra.crawler.article.db

import com.jaitlapps.kasandra.crawler.article.db.table.CrawledSitePage

import scala.concurrent.Future

trait CrawledSitePagesDao {
  def save(page: CrawledSitePage): Future[Int]
}
