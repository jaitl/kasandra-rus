package com.jaitlapps.kasandra.crawler.parser.impl

import org.scalatest.FunSuite
import org.scalatest.Matchers

import scala.io.Source

class VestiSiteParserTest extends FunSuite with Matchers {
  test("parse vesti 1") {
    val html = Source.fromURL(getClass.getResource("/newsSites/vesti/vesti1.html")).mkString

    val result = VestiSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Советник Трампа покинул Совет нацбезопасности"

    content.startsWith("Из состава Совета национальной безопасности Белого дома") shouldBe true
    content.endsWith("никаких потенциальных проблем для администрации президента Трампа.") shouldBe true
  }
}
