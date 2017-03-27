package com.jaitlapps.kasandra.crawler.parsers

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaitlapps.kasandra.crawler.models.CrawledVkUrl
import collection.JavaConverters._

object VkWallParser {

  private val regex = "(https?)://[^\\s/$.?#].[^\\s]*".r

  def parseJson(data: String): Set[CrawledVkUrl] = {
    val mapper = new ObjectMapper()
    val tree = mapper.readTree(data)
    val nodes = tree.get("response").get("items").elements().asScala.toSet

    nodes.flatMap(jNode => {
      val date = jNode.get("date").asLong() * 1000
      val post = jNode.get("text").asText()

      val urls = parseUrls(post).map(u => CrawledVkUrl(u, date))

      if (jNode.has("attachments")) {
        val link = jNode.get("attachments").asScala.toSeq.find(att => att.get("type").asText() == "link")
        if(link.isDefined) {
          val urlLink = link.get.get("link").get("url").asText()
          urls :+ CrawledVkUrl(urlLink, date)
        } else {
          urls
        }
      } else {
        urls
      }
    })
  }

  def parseWallSize(data: String): Int = {
    val mapper = new ObjectMapper()
    val tree = mapper.readTree(data)

    tree.get("response").get("count").asInt()
  }

  private def parseUrls(text: String): Seq[String] = regex.findAllIn(text).toSeq
}
