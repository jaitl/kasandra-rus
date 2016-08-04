package com.jaitlapps.kasandra.crawler.actors

import java.io.PrintWriter
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorLogging, Props}
import com.jaitlapps.kasandra.crawler.actors.FileSaveActor.SavePage
import com.jaitlapps.kasandra.crawler.config.SaveConfig
import com.jaitlapps.kasandra.crawler.json.JsonExtension
import com.jaitlapps.kasandra.crawler.models.CrawledSitePage
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._


class FileSaveActor(conf: SaveConfig) extends Actor with ActorLogging with JsonExtension {

  implicit val formats = Serialization.formats(NoTypeHints) + UUIDSerialiser

  private val dt = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss")

  private val file: PrintWriter = {
    val fileName = dt.format(new Date)
    new PrintWriter(Paths.get(conf.path, fileName).toFile)
  }

  override def receive: Receive = {
    case SavePage(page) => {
      log.info(s"Save page: ${page.title}")
      file.println(write(page))
      file.flush()
    }
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    file.close()
    super.postStop()
  }
}

object FileSaveActor {
  case class SavePage(page: CrawledSitePage)

  def props(conf: SaveConfig): Props = Props(new FileSaveActor(conf))
}
