package com.jaitlapps.kasandra.crawler.parser.impl

import org.scalatest.FunSuite
import org.scalatest.Matchers

import scala.io.Source

class OneTvSiteParserTest extends FunSuite with Matchers {
  test("parse 1vt 1") {
    val html = Source.fromURL(getClass.getResource("/newsSites/1tv/1tv1.html")).mkString

    val result = OneTvSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Полиция Монако ищет преступников, ограбивших ювелирный салон в центре Монте-Карло"

    content.startsWith("Масштабная спецоперация в Монако") shouldBe true
    content.endsWith("судя по всему, речь о миллионах евро.") shouldBe true
  }
}
