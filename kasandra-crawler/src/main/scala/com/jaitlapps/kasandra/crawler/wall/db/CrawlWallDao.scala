package com.jaitlapps.kasandra.crawler.wall.db

import java.util.UUID

import com.jaitlapps.kasandra.crawler.wall.db.table.CrawlWall

import scala.concurrent.Future

trait CrawlWallDao {
  def findNotCrawledWalls(): Future[Seq[CrawlWall]]
  def getCrawlWallList(): Future[Seq[CrawlWall]]
  def updateOffset(id: UUID, offset: Int): Future[Int]
}
