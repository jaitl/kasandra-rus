package com.jaitlapps.kasandra.crawler

import java.util.concurrent.Executors

import akka.actor.ActorRef
import akka.actor.ActorRefFactory
import akka.actor.ActorSystem
import com.jaitlapps.kasandra.crawler.WallCrawlerApp.dbConnection
import com.jaitlapps.kasandra.crawler.actor.ActorCreator
import com.jaitlapps.kasandra.crawler.site.actor.SiteCrawlerActor
import com.jaitlapps.kasandra.crawler.site.actor.SiteCrawlerActor.SiteCrawlerConfig
import com.jaitlapps.kasandra.crawler.site.actor.SiteDispatcherActor
import com.jaitlapps.kasandra.crawler.site.db.CrawledSitePagesDaoSlick
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.db.DbInit
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.raw.db.RawCrawledPagesDaoSlick
import com.jaitlapps.kasandra.crawler.wall.db.CrawlWallDaoSlick
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDaoSlick
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success

object SiteCrawlerApp extends App with StrictLogging {
  val system = ActorSystem("KasandraWallCrawlerSystem")
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(10))

  val config = ConfigFactory.load()
  val siteConfig = config.getConfig("site")
  val siteCrawlerConfig = SiteCrawlerConfig(siteConfig.getConfig("crawler"))

  val dbConnection = new DbConnection
  val dbInit = new DbInit(dbConnection)
  val wallLinksDao = new WallLinksDaoSlick(dbConnection)
  val crawledSitePagesDao = new CrawledSitePagesDaoSlick(dbConnection)
  val crawlWallDao = new CrawlWallDaoSlick(dbConnection)
  val rawCrawledPagesDao = new RawCrawledPagesDaoSlick(dbConnection)

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

  val siteDispatcherActor = system.actorOf(SiteDispatcherActor.props(siteCrawlerActorCreator))

  dbInit.init()
    .flatMap(_ => crawlWallDao.getCrawlWallList()
      .map(_.map(wall => CrawlSite(wall.id, wall.siteType, wall.domain, wall.vkGroup))))
    .onComplete {
      case Success(sites) =>
        siteDispatcherActor ! SiteDispatcherActor.StartSiteCrawl(sites)
      case Failure(ex) =>
        logger.error("Db init error", ex)
        system.terminate()
    }
}
