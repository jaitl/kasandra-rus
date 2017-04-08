package com.jaitlapps.kasandra.crawler.parser.impl

import org.scalatest.FunSuite
import org.scalatest.Matchers

import scala.io.Source

class RbcSiteParserTest extends FunSuite with Matchers {
  test("parse rbc 1") {
    val html = Source.fromURL(getClass.getResource("/newsSites/rbc/rbc1.html")).mkString

    val result = RbcSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Презентация багги чеченского производства Кадырову"

    content.startsWith("В субботу на заводе «Чеченавто» в городе Аргун прошла презентация") shouldBe true
    content.endsWith("в условиях сильно пересеченной местности.") shouldBe true
  }

  test("parse rbc 2") {
    val html = Source.fromURL(getClass.getResource("/newsSites/rbc/rbc2.html")).mkString

    val result = RbcSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "СКР подсчитал выгоду от мошенничества экс-депутата Вороненкова"

    content.startsWith("Бежавший из страны Денис Вороненков, которого обвиняют в рейдерском захвате") shouldBe true
    content.endsWith("По его словам, вся доказательная база сводится к показаниям других фигурантов.") shouldBe true
  }
}
