package com.jaitlapps.kasandra.crawler.parser.impl

import org.scalatest.FunSuite
import org.scalatest.Matchers

import scala.io.Source

class KpSiteParserTest extends FunSuite with Matchers {
  test("parse kp 1") {
    val html = Source.fromURL(getClass.getResource("/newsSites/kp/kp1.html")).mkString

    val result = KpSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Россияне почти готовы тратить"

    content.startsWith("Потребительские настроения россиян улучшились") shouldBe true
    content.endsWith("реальная заработная плата выросла на 2,3%, - утверждает чиновник.") shouldBe true
  }

  test("parse kp 2") {
    val html = Source.fromURL(getClass.getResource("/newsSites/kp/kp2.html")).mkString

    val result = KpSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Захарова прокомментировала «рассекреченное» фото Путина и Симоньян"

    content.startsWith("Официальный представитель МИД РФ Мария Захарова") shouldBe true
    content.endsWith("что у Симоньян есть огромная харизма - и даже две».") shouldBe true
  }
}
