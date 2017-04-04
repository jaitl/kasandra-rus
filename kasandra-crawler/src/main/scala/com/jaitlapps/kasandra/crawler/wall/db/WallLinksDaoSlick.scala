package com.jaitlapps.kasandra.crawler.wall.db

import java.util.UUID

import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLinksTable

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random

class WallLinksDaoSlick(
  override val dbConnection: DbConnection
)(implicit executionContext: ExecutionContext) extends WallLinksDao with WallLinksTable {
  import dbConnection.profile.api._

  val db = dbConnection.db

  override def save(wallLink: WallLink): Future[Int] = db.run {
    wallLinkQuery += wallLink
  }

  override def saveBatch(wallLinks: Seq[WallLink]): Future[Option[Int]] = db.run {
    wallLinkQuery ++= wallLinks
  }

  override def findRandomNotDownloadedLink(siteType: SiteType): Future[Option[WallLink]] = {
    val linkQuery = wallLinkQuery
      .filter(_.siteType === siteType)
      .filterNot(_.isFailed)
      .filterNot(_.isDownloaded)

    db.run(linkQuery.result)
      .map(links => Random.shuffle(links).headOption)
  }

  override def markAsDownloaded(id: UUID): Future[Int] = db.run {
    wallLinkQuery
      .filter(_.id === id)
      .map(_.isDownloaded)
      .update(true)
  }

  override def markAsFailed(id: UUID): Future[Int] = db.run {
    wallLinkQuery
      .filter(_.id === id)
      .map(_.isFailed)
      .update(true)
  }
}
