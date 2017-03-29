package com.jaitlapps.kasandra.crawler

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.db.DbInit
import com.jaitlapps.kasandra.crawler.wall.actor.WallDispatcherActor
import com.jaitlapps.kasandra.crawler.wall.db.CrawlWallDaoSlick
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDaoSlick
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success

object WallCrawlerApp extends App with StrictLogging {
  val system = ActorSystem("KasandraWallCrawlerSystem")
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(10))

  val dbConnection = new DbConnection
  val dbInit = new DbInit(dbConnection)
  val wallLinksDao = new WallLinksDaoSlick(dbConnection)
  val crawlWallDao = new CrawlWallDaoSlick(dbConnection)

  val wallDispatcherActor = system.actorOf(
    props = WallDispatcherActor.props(wallLinksDao, crawlWallDao, executionContext),
    name = WallDispatcherActor.name()
  )

  dbInit.init().flatMap(_ => crawlWallDao.findNotCrawledWalls())
    .onComplete {
    case Success(walls) =>
      wallDispatcherActor ! WallDispatcherActor.StartCrawling(walls)
    case Failure(ex) =>
      logger.error("Db init error", ex)
      system.terminate()
  }
}
