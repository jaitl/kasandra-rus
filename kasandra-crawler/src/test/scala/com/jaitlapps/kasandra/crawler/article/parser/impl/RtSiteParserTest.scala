package com.jaitlapps.kasandra.crawler.article.parser.impl

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class RtSiteParserTest extends FlatSpec with Matchers {
  it should "parse rt site" in {
    val html = Source.fromURL(getClass.getResource("/newsSites/rt1.html")).mkString

    val result = RtSiteParser.parse(html)

    result.title shouldBe "Власти США разрешили частной компании отправить экспедицию на Луну"
    result.annotation should startWith ("Правительство США выдало разрешение частной компании " +
      "Moon Express на отправку экспедиции на Луну.")
    result.content.contains("цитирует слова главы компании Боба Ричардса ТАСС.") shouldBe true
  }
}
