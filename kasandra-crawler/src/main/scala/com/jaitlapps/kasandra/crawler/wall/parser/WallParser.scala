package com.jaitlapps.kasandra.crawler.wall.parser

import java.sql.Timestamp

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaitlapps.kasandra.crawler.models.CrawledWallUrl

import scala.collection.JavaConverters._

object WallParser {

  private val regex = "(https?)://[^\\s/$.?#].[^\\s]*".r

  def parseJson(data: String): Set[CrawledWallUrl] = {
    val mapper = new ObjectMapper()
    val tree = mapper.readTree(data)
    val nodes = tree.get("response").get("items").elements().asScala.toSet

    nodes.flatMap(jNode => {
      val date = jNode.get("date").asLong() * 1000
      val post = jNode.get("text").asText()

      val urls = parseUrls(post).map(u => CrawledWallUrl(u, new Timestamp(date)))

      if (jNode.has("attachments")) {
        val link = jNode.get("attachments").asScala.toSeq.find(att => att.get("type").asText() == "link")
        if(link.isDefined) {
          val urlLink = link.get.get("link").get("url").asText()
          urls :+ CrawledWallUrl(urlLink, new Timestamp(date))
        } else {
          urls
        }
      } else {
        urls
      }
    })
  }

  private def parseUrls(text: String): Seq[String] = regex.findAllIn(text).toSeq
}
