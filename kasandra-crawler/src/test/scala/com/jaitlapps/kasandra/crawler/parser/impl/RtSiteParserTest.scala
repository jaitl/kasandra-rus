package com.jaitlapps.kasandra.crawler.parser.impl

import org.scalatest.FlatSpec
import org.scalatest.FunSuite
import org.scalatest.Matchers

import scala.io.Source

class RtSiteParserTest extends FunSuite with Matchers {
  test("parse rt site 1") {
    val html = Source.fromURL(getClass.getResource("/newsSites/rt/rt1.html")).mkString

    val result = RtSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Власти США разрешили частной компании отправить экспедицию на Луну"
    content.startsWith("Правительство США выдало разрешение частной компании Moon Express") shouldBe true
    content.contains("«Правительство США приняло поистине историческое решение позволить") shouldBe true
    content.endsWith("Moon Express станет первой компанией, вышедшей в глубокий космос.") shouldBe true
  }

  test("parse rt site 2") {
    val html = Source.fromURL(getClass.getResource("/newsSites/rt/rt2.html")).mkString

    val result = RtSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Чужими руками: как разоблачение шпионской программы ЦРУ повлияет на миф о русских хакерах"
    content.startsWith("Проведение кибератак от третьего лица — не единственный вариант использования") shouldBe true
    content.contains("Говорят на одном языке") shouldBe true
    content.endsWith("Госдепартамента и курируют регионы Европы, Ближнего Востока и Африки.") shouldBe true
  }
}
