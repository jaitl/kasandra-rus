package com.jaitlapps.kasandra.crawler

import java.util.concurrent.Executors

import akka.actor.ActorRef
import akka.actor.ActorRefFactory
import akka.actor.ActorSystem
import com.jaitlapps.kasandra.crawler.actor.ActorCreator
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.db.DbInit
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.raw.db.RawCrawledPagesDaoSlick
import com.jaitlapps.kasandra.crawler.site.actor.SiteCrawlerActor
import com.jaitlapps.kasandra.crawler.site.actor.SiteCrawlerActor.SiteCrawlerConfig
import com.jaitlapps.kasandra.crawler.site.actor.SiteDispatcherActor
import com.jaitlapps.kasandra.crawler.site.db.CrawledSitePagesDaoSlick
import com.jaitlapps.kasandra.crawler.wall.actor.WallCrawlerActor
import com.jaitlapps.kasandra.crawler.wall.actor.WallCrawlerActor.WallCrawlerConfig
import com.jaitlapps.kasandra.crawler.wall.actor.WallDispatcherActor
import com.jaitlapps.kasandra.crawler.wall.db.CrawlWallDaoSlick
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDaoSlick
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success

object CrawlerApp extends App with StrictLogging {
  val system = ActorSystem("KasandraCrawlerSystem")
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(10))

  val config = ConfigFactory.load()

  val siteConfig = config.getConfig("site")
  val wallConfig = config.getConfig("wall")

  val siteCrawlerConfig = SiteCrawlerConfig(siteConfig.getConfig("crawler"))
  val wallCrawlerConfig = WallCrawlerConfig(wallConfig.getConfig("crawler"))

  val dbConnection = new DbConnection
  val dbInit = new DbInit(dbConnection)
  val wallLinksDao = new WallLinksDaoSlick(dbConnection)
  val crawledSitePagesDao = new CrawledSitePagesDaoSlick(dbConnection)
  val crawlWallDao = new CrawlWallDaoSlick(dbConnection)
  val rawCrawledPagesDao = new RawCrawledPagesDaoSlick(dbConnection)

  val wallCrawlerActorCreator: ActorCreator[CrawlSite] = new ActorCreator[CrawlSite] {
    override def create(factory: ActorRefFactory, name: String): (CrawlSite) => ActorRef =
      site => factory.actorOf(
        props = WallCrawlerActor.props(
          site = site,
          crawlWallDao = crawlWallDao,
          wallLinksDao = wallLinksDao,
          rawCrawledPagesDao = rawCrawledPagesDao,
          config = wallCrawlerConfig,
          executionContext = executionContext
        ),
        name = name
      )
  }

  val siteCrawlerActorCreator = new ActorCreator[CrawlSite] {
    override def create(factory: ActorRefFactory, name: String): (CrawlSite) => ActorRef =
      site => factory.actorOf(
        props = SiteCrawlerActor.props(
          site = site,
          config = siteCrawlerConfig,
          crawledSitePagesDao = crawledSitePagesDao,
          wallLinksDao = wallLinksDao,
          rawCrawledPagesDao = rawCrawledPagesDao,
          executionContext = executionContext
        ),
        name = name
      )
  }

  val wallDispatcherActor = system.actorOf(
    props = WallDispatcherActor.props(wallCrawlerActorCreator),
    name = WallDispatcherActor.name()
  )

  val siteDispatcherActor = system.actorOf(
    props = SiteDispatcherActor.props(siteCrawlerActorCreator),
    name = SiteDispatcherActor.name()
  )

  dbInit.init()
    .flatMap(_ => crawlWallDao.findNotCrawledWalls())
    .zip(crawlWallDao.getCrawlWallList()
      .map(_.map(wall => CrawlSite(wall.id, wall.siteType, wall.domain, wall.vkGroup))))
    .onComplete {
      case Success((walls, sites)) =>
        wallDispatcherActor ! WallDispatcherActor.StartCrawling(walls)
        siteDispatcherActor ! SiteDispatcherActor.StartSiteCrawl(sites)
      case Failure(ex) =>
        logger.error("Error during start app", ex)
        system.terminate()
    }
}
