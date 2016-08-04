package com.jaitlapps.kasandra.crawler.parsers.impl

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class RiaSiteParserTest extends FlatSpec with Matchers {
  it should "parse ria site" in {
    val html = Source.fromURL(getClass.getResource("/newsSites/ria1.html")).mkString

    val result = RiaSiteParser.parse(html)

    result.title shouldBe "Посчитали по низам: о чем говорит рейтинг разгула коррупции в России"
    result.annotation should startWith ("МОСКВА, 3 авг. Наталья Дембинская, обозреватель МИА \"Россия сегодня\".")
    result.content.contains("По данным прокурорской статистики, " +
      "Курская область  - рекордсмен по числу зарегистрированных преступлений")
  }
}
