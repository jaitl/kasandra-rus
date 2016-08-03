package com.jaitlapps.kasandra.crawler.actors

import akka.actor.{Actor, ActorLogging}
import com.jaitlapps.kasandra.crawler.actors.FileSaveActor.SavePage
import com.jaitlapps.kasandra.crawler.models.CrawledSitePage

class FileSaveActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case SavePage(page) => {
      log.info(s"Save page: ${page.title}")
    }
  }
}

object FileSaveActor {
  case class SavePage(page: CrawledSitePage)
}
