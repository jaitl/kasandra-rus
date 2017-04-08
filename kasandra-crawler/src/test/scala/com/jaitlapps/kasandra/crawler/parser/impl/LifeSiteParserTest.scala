package com.jaitlapps.kasandra.crawler.parser.impl

import org.scalatest.FunSuite
import org.scalatest.Matchers

import scala.io.Source

class LifeSiteParserTest extends FunSuite with Matchers {
  test("parse life 1") {
    val html = Source.fromURL(getClass.getResource("/newsSites/life/life1.html")).mkString

    val result = LifeSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Британские учёные доказали, что сон на работе полезен для здоровья"

    content.startsWith("30-минутный сон повышает продуктивность работы") shouldBe true
    content.contains("Британские учёные установили, что короткий сон во") shouldBe true
    content.endsWith("повышает продуктивность работы, сфокусированность и креативность.") shouldBe true
  }

  test("parse life 2") {
    val html = Source.fromURL(getClass.getResource("/newsSites/life/life2.html")).mkString

    val result = LifeSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Список погибших и пострадавших военных при ударе США по базе в Хомсе"

    content.startsWith("Лайф публикует список погибших военных и пострадавших") shouldBe true
    content.endsWith("\"Томагавк\" по авиабазе Шайрат в Хомсе.") shouldBe true
  }

  test("parse life 3") {
    val html = Source.fromURL(getClass.getResource("/newsSites/life/life3.html")).mkString

    val result = LifeSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Фото и видео с места наезда грузовика на толпу в Стокгольме"

    content.startsWith("Сегодня грузовик въехал в людей в Стокгольме.") shouldBe true
    content.endsWith("Данные о ЧП выясняются.") shouldBe true
  }
}
