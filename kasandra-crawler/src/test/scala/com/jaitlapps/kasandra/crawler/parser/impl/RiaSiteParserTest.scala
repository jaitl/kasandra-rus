package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.exceptions.ParseException
import org.scalatest.FunSuite
import org.scalatest.Matchers

import scala.io.Source

class RiaSiteParserTest extends FunSuite with Matchers {
  test("parse ria 1") {
    val html = Source.fromURL(getClass.getResource("/newsSites/ria/ria1.html")).mkString

    val result = RiaSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Посчитали по низам: о чем говорит рейтинг разгула коррупции в России"

    content.startsWith("Коррупция для России уже не проблема, а огромная беда, накрывшая страну.") shouldBe true
    content.endsWith("Чиновника задержали в июле 2013 года. Свою вину Урлашов в суде не признал.") shouldBe true
  }

  test("parse ria 2") {
    val html = Source.fromURL(getClass.getResource("/newsSites/ria/ria2.html")).mkString

    val result = RiaSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Взрыв возле велотрассы в Рио произвели саперы"
    content.startsWith("Взрыв, произошедший в районе финиша мужской велогонки на Олимпиаде-2016 в Рио") shouldBe true
    content.endsWith("так как участники гонки доедут до этого участка нескоро.") shouldBe true
  }

  test("parse empty content") {
    val html = Source.fromURL(getClass.getResource("/newsSites/ria/ria3_null.html")).mkString

    the [ParseException] thrownBy RiaSiteParser.parse(html)
  }
}
