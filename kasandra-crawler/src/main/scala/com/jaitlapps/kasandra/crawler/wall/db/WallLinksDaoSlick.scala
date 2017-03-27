package com.jaitlapps.kasandra.crawler.wall.db

import com.jaitlapps.kasandra.crawler.db.DbConnection

import scala.concurrent.Future

class WallLinksDaoSlick(override val dbConnection: DbConnection) extends WallLinksDao with WallLinksTable {
  import dbConnection.profile.api._
  val db = dbConnection.db

  override def save(wallLink: WallLink): Future[Int] = db.run {
    wallLinkQuery += wallLink
  }
}
