package com.jaitlapps.kasandra.crawler.parser.impl

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.io.Source

class RtSiteParserTest extends FlatSpec with Matchers {
  it should "parse rt site" in {
    val html = Source.fromURL(getClass.getResource("/newsSites/rt/rt1.html")).mkString

    val result = RtSiteParser.parse(html)

    result.title shouldBe "Власти США разрешили частной компании отправить экспедицию на Луну"
    result.content.contains("цитирует слова главы компании Боба Ричардса ТАСС.") shouldBe true
  }
}
