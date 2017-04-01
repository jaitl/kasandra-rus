package com.jaitlapps.kasandra.crawler.wall.db

import java.util.UUID

import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink

import scala.concurrent.Future

trait WallLinksDao {
  def save(wallLink: WallLink): Future[Int]
  def saveBatch(wallLinks: Seq[WallLink]): Future[Option[Int]]
  def findRandomNotDownloadedLink(siteType: SiteType): Future[Option[WallLink]]
  def markAsDownloaded(id: UUID): Future[Int]
  def markAsFailed(id: UUID): Future[Int]
}
