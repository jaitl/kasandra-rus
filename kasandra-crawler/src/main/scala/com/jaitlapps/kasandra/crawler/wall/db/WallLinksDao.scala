package com.jaitlapps.kasandra.crawler.wall.db

import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink

import scala.concurrent.Future

trait WallLinksDao {
  def save(wallLink: WallLink): Future[Int]
  def saveBatch(wallLinks: Seq[WallLink]): Future[Option[Int]]
}
