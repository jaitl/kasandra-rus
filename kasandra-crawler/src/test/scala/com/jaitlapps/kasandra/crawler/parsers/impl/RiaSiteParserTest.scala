package com.jaitlapps.kasandra.crawler.parsers.impl

import com.jaitlapps.kasandra.crawler.exceptions.ParseException
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class RiaSiteParserTest extends FlatSpec with Matchers {
  it should "parse ria site" in {
    val html = Source.fromURL(getClass.getResource("/newsSites/ria1.html")).mkString

    val result = RiaSiteParser.parse(html)

    result.title shouldBe "Посчитали по низам: о чем говорит рейтинг разгула коррупции в России"
    result.annotation should startWith ("МОСКВА, 3 авг. Наталья Дембинская, обозреватель МИА \"Россия сегодня\".")
    result.annotation should have length 463
    result.content.contains("По данным прокурорской статистики, " +
      "Курская область  - рекордсмен по числу зарегистрированных преступлений")
  }

  it should "parse annotation" in {
    val html = Source.fromURL(getClass.getResource("/newsSites/ria2_ann.html")).mkString

    val result = RiaSiteParser.parse(html)

    result.title shouldBe "Взрыв возле велотрассы в Рио произвели саперы"
    result.annotation should startWith ("МОСКВА, 6 авг — РИА Новости. Взрыв, произошедший в районе финиша мужской велогонки на Олимпиаде-2016 в Рио")
    result.annotation should have length 500
    result.content.contains("В результате инцидента никто не пострадал.")
  }

  it should "parse empty content" in {
    val html = Source.fromURL(getClass.getResource("/newsSites/ria3_null.html")).mkString

    the [ParseException] thrownBy RiaSiteParser.parse(html)
  }
}
