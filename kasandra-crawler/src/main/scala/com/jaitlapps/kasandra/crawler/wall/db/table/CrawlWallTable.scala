package com.jaitlapps.kasandra.crawler.wall.db.table

import java.util.UUID

import com.jaitlapps.kasandra.crawler.db.CustomTypes
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.models.SiteType
import slick.lifted.ProvenShape

case class CrawlWall(
  id: UUID,
  siteType: SiteType,
  domain: String,
  vkGroup: String,
  totalWallSize: Int,
  currentOffset: Int
)

trait CrawlWallTable extends CustomTypes {
  val dbConnection: DbConnection

  import dbConnection.profile.api._

  protected class CrawlWalls(tag: Tag) extends Table[CrawlWall](tag, "CrawlWalls") {
    val id: Rep[UUID] = column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))

    val siteType: Rep[SiteType] = column[SiteType]("siteType")

    val domain: Rep[String] = column[String]("domain")

    val vkGroup: Rep[String] = column[String]("vkGroup")

    val totalWallSize: Rep[Int] = column[Int]("totalWallSize")

    val currentOffset: Rep[Int] = column[Int]("currentOffset")

    override def * : ProvenShape[CrawlWall] = (id, siteType, domain, vkGroup, totalWallSize,
      currentOffset) <> (CrawlWall.tupled, CrawlWall.unapply)
  }

  protected val crawlWallsQuery: TableQuery[CrawlWalls] = TableQuery[CrawlWalls]
}
