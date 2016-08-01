package com.jaitlapps.kasandra.crawler.parsers

import com.jaitlapps.kasandra.crawler.models.CrawledVkUrl

object VkWallParser {
  import org.json4s._
  import org.json4s.native.JsonMethods._

  def parseJson(data: String): CrawledVkUrl = {
    val json = parse(data)

    val items = (json \ "response") \ " items"

    CrawledVkUrl("", 0)
  }
}
