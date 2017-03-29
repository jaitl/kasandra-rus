package com.jaitlapps.kasandra.crawler.wall.db

import java.util.UUID

import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.wall.db.table.CrawlWall
import com.jaitlapps.kasandra.crawler.wall.db.table.CrawlWallTable

import scala.concurrent.Future

class CrawlWallDaoSlick(override val dbConnection: DbConnection) extends CrawlWallDao with CrawlWallTable {
  import dbConnection.profile.api._

  private val db = dbConnection.db

  override def findNotCrawledWalls(): Future[Seq[CrawlWall]] = db.run {
    crawlWallsQuery
      .filter(wall => wall.currentOffset < wall.totalWallSize)
      .result
  }


  override def getCrawlWallList(): Future[Seq[CrawlWall]] = db.run {
    crawlWallsQuery.result
  }

  override def updateOffset(id: UUID, offset: Int): Future[Int] = db.run {
    crawlWallsQuery
      .filter(_.id === id)
      .map(_.currentOffset)
      .update(offset)
  }
}
