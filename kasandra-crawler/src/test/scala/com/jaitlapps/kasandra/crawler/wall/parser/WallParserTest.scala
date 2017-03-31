package com.jaitlapps.kasandra.crawler.wall.parser

import java.sql.Timestamp

import com.jaitlapps.kasandra.crawler.models.CrawledWallUrl
import org.scalatest.Matchers
import org.scalatest.WordSpec

import scala.io.Source

class WallParserTest extends WordSpec with Matchers {
  "vk wall parser" should {
    "parse json" in {
      val json = Source.fromURL(getClass.getResource("/vkWall/response10.json")).mkString

      val result = WallParser.parseJson(json)

      result should have size 10
      result should contain(
        CrawledWallUrl("https://russian.rt.com/article/314862-brifing-minoborony-rf-po-situacii-v-sirii", new Timestamp(1470063137000L))
      )
    }
  }
}
