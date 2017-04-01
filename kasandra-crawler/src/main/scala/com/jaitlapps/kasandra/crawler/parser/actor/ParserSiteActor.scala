package com.jaitlapps.kasandra.crawler.parser.actor

import akka.actor.Actor
import akka.actor.ActorLogging
import com.jaitlapps.kasandra.crawler.wall.db.table.WallLink

class ParserSiteActor() extends Actor with ActorLogging {

  override def receive: Receive = ???
  /*
  {

    case ParseSitePage(wallLink, data) =>
      Try(ParserFactory.getParser(site).parse(data)) match {
        case Success(page) =>

          val crawledSitePage = CrawledSitePage(
            UUID.randomUUID(), wallLink.timestamp, page.title, page.annotation, page.content, wallLink.url
          )

          val pageSaveResultFuture = for {
            _ <- crawledSitePagesDao.save(crawledSitePage)
            _ <- wallLinksDao.markAsDownloaded(wallLink.id)
          } yield ScheduleUrlSiteCrawl


        case Failure(ex) =>
          log.error(ex, s"Parse error, url: ${wallLink.url}")
      }
  }
  */
}

object ParserSiteActor {
  case class ParseSitePage(wallLink: WallLink, data: String)

}