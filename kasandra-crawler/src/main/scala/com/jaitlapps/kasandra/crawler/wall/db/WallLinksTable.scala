package com.jaitlapps.kasandra.crawler.wall.db

import java.sql.Timestamp
import java.util.UUID

import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.models.SiteType
import slick.lifted.ProvenShape

trait WallLinksTable {
  val dbConnection: DbConnection

  import dbConnection.profile.api._

  implicit val siteTypeType = MappedColumnType.base[SiteType, String](_.name, SiteType.apply)

  protected class WallLinks(tag: Tag) extends Table[WallLink](tag, "WallLinks") {
    val id: Rep[UUID] = column[UUID]("id", O.PrimaryKey, O.SqlType("UUID"))

    val timestamp: Rep[Timestamp] = column[Timestamp]("timestamp")

    val siteType: Rep[SiteType] = column[SiteType]("siteType")

    val url: Rep[String] = column[String]("url")

    override def * : ProvenShape[WallLink] = (id, timestamp, siteType, url) <> (WallLink.tupled, WallLink.unapply)
  }

  protected val wallLinkQuery: TableQuery[WallLinks] = TableQuery[WallLinks]
}

case class WallLink(id: UUID, timestamp: Timestamp, siteType: SiteType, url: String)