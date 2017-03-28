package com.jaitlapps.kasandra.crawler.wall.db

import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLinksTable

import scala.concurrent.Future

class WallLinksDaoSlick(override val dbConnection: DbConnection) extends WallLinksDao with WallLinksTable {
  import dbConnection.profile.api._
  val db = dbConnection.db

  override def save(wallLink: WallLink): Future[Int] = db.run {
    wallLinkQuery += wallLink
  }

  override def saveBatch(wallLinks: Seq[WallLink]): Future[Option[Int]] = db.run {
    wallLinkQuery ++= wallLinks
  }
}
