package com.jaitlapps.kasandra.crawler.parser.impl

import org.scalatest.FunSuite
import org.scalatest.Matchers

import scala.io.Source

class LentaSiteParserTest extends FunSuite with Matchers {
  test("parse lenta 1") {
    val html = Source.fromURL(getClass.getResource("/newsSites/lenta/lenta1.html")).mkString

    val result = LentaSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "«Послужите! Людьми станете»"

    content.startsWith("В Государственной Думе предложили") shouldBe true
    content.endsWith("стоит ли уклоняться от службы в сегодняшней российской армии.") shouldBe true
  }

  test("parse lenta 2") {
    val html = Source.fromURL(getClass.getResource("/newsSites/lenta/lenta2.html")).mkString

    val result = LentaSiteParser.parse(html)
    val content = result.content

    result.title shouldBe "Умер создатель японского эйсид-синтезатора TB-303 и крестный отец MIDI"

    content.startsWith("Японский инженер и основатель") shouldBe true
    content.endsWith("и программного обеспечения для создания звука.") shouldBe true
  }
}
