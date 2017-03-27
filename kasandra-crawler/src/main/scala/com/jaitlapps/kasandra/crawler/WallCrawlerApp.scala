package com.jaitlapps.kasandra.crawler

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import com.jaitlapps.kasandra.crawler.db.DbConnection
import com.jaitlapps.kasandra.crawler.db.DbInit
import com.jaitlapps.kasandra.crawler.models.{CrawlSite, SiteType}
import com.jaitlapps.kasandra.crawler.wall.actor.WallDispatcherActor
import com.jaitlapps.kasandra.crawler.wall.db.WallLinksDaoSlick
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success

object WallCrawlerApp extends App with StrictLogging {
  val system = ActorSystem("KasandraWallCrawlerSystem")
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(10))

  val sites = Seq(
    CrawlSite(siteType = SiteType.RiaSite, domain = "ria.ru", vkGroup = "ria")
  // CrawlSite(siteType = SiteType.RtSite, domain = "russian.rt.com", vkGroup = "rt_russian")
  )

  val dbInit = new DbInit(new DbConnection)
  val wallLinksDao = new WallLinksDaoSlick(new DbConnection)

  val wallDispatcherActor = system.actorOf(
    props = WallDispatcherActor.props(wallLinksDao, executionContext),
    name = WallDispatcherActor.name()
  )

  dbInit.init().onComplete {
    case Success(_) =>
      wallDispatcherActor ! WallDispatcherActor.StartCrawling(sites)
    case Failure(ex) =>
      logger.error("Db init error", ex)
      system.terminate()
  }
}
