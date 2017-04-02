package com.jaitlapps.kasandra.crawler.db

import com.jaitlapps.kasandra.crawler.raw.db.table.RawCrawledPagesTable
import com.jaitlapps.kasandra.crawler.site.db.table.CrawledSitePagesTable
import com.jaitlapps.kasandra.crawler.wall.db.table.CrawlWallTable
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLinksTable
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class DbInit(override val dbConnection: DbConnection)(implicit val executionContext: ExecutionContext)
  extends WallLinksTable
  with CrawlWallTable
  with CrawledSitePagesTable
  with RawCrawledPagesTable {

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
      _ <- if (!tableNames.contains(crawlWallsQuery.baseTableRow.tableName)) {
        db.run(crawlWallsQuery.schema.create)
      } else {
        Future.successful()
      }
      _ <- if (!tableNames.contains(crawledSitePagesQuery.baseTableRow.tableName)) {
        db.run(crawledSitePagesQuery.schema.create)
      } else {
        Future.successful()
      }
      _ <- if (!tableNames.contains(rawCrawledPagesQuery.baseTableRow.tableName)) {
        db.run(rawCrawledPagesQuery.schema.create)
      } else {
        Future.successful()
      }
    } yield ()
}
