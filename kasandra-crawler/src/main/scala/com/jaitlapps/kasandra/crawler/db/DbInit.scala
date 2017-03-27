package com.jaitlapps.kasandra.crawler.db

import com.jaitlapps.kasandra.crawler.wall.db.WallLinksTable
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DbInit(override val dbConnection: DbConnection)(implicit val executionContext: ExecutionContext)
  extends WallLinksTable {

  private val db = dbConnection.db
  import dbConnection.profile.api._

  def init(): Future[Unit] =
    for {
      tables <- db.run(MTable.getTables)
      tableNames = tables.map(_.name.name)
      _ <- if (!tableNames.contains(wallLinkQuery.baseTableRow.tableName)) {
        db.run(wallLinkQuery.schema.create)
      } else {
        Future.successful()
      }
    } yield ()
}
