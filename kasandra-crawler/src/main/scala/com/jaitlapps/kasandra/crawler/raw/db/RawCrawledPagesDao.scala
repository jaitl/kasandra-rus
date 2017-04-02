package com.jaitlapps.kasandra.crawler.raw.db

import java.util.UUID

import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPage
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink

import scala.concurrent.Future

trait RawCrawledPagesDao {
  def save(raw: RawCrawledPage): Future[Int]
  def markAsParsed(id: UUID): Future[Int]
  def markAsFailed(id: UUID): Future[Int]
  def crawledPagesWithLink(siteTypes: Set[SiteType]): Future[Seq[(RawCrawledPage, WallLink)]]
}
