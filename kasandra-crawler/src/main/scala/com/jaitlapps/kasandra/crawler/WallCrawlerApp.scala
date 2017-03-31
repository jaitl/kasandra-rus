package com.jaitlapps.kasandra.crawler

import java.util.concurrent.Executors

import akka.actor.ActorRef
import akka.actor.ActorRefFactory
import akka.actor.ActorSystem
import com.jaitlapps.kasandra.crawler.actor.ActorCreator
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.db.DbInit
import com.jaitlapps.kasandra.crawler.db.dao.RawCrawledPagesDaoSlick
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.wall.actor.WallCrawlerActor
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
  val rawCrawledPagesDao = new RawCrawledPagesDaoSlick(dbConnection)

  val wallCrawlerActorCreator: ActorCreator[(ActorRef, CrawlSite)] = new ActorCreator[(ActorRef, CrawlSite)] {
    override def create(factory: ActorRefFactory, name: String): ((ActorRef, CrawlSite)) => ActorRef = {
      case (dispatcher, site) => factory.actorOf(
        props = WallCrawlerActor.props(
          site = site,
          crawlWallDao = crawlWallDao,
          wallLinksDao = wallLinksDao,
          rawCrawledPagesDao = rawCrawledPagesDao,
          wallDispatcherActor = dispatcher,
          executionContext = executionContext
        ),
        name = name
      )
    }
  }

  val wallDispatcherActor = system.actorOf(
    props = WallDispatcherActor.props(wallCrawlerActorCreator),
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
