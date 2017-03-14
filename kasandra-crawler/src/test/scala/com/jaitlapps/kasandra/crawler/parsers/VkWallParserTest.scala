package com.jaitlapps.kasandra.crawler.parsers

import com.jaitlapps.kasandra.crawler.models.CrawledVkUrl
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class VkWallParserTest extends FlatSpec with Matchers {
  "vk wall parser" should "parse json" in {
    val json = Source.fromURL(getClass.getResource("/vkWall/response10.json")).mkString

    val result = VkWallParser.parseJson(json)

    result should have size 10
    result should contain (CrawledVkUrl("https://russian.rt.com/article/314862-brifing-minoborony-rf-po-situacii-v-sirii", 1470063137L))
  }
}
