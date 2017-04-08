package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.exceptions.ParseException
import com.jaitlapps.kasandra.crawler.parser.ParsedPage
import com.jaitlapps.kasandra.crawler.parser.SiteParser
import com.jaitlapps.kasandra.crawler.utils.HtmlUtils
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object RiaSiteParser extends SiteParser {
  override def parse(html: String): ParsedPage = {
    val document = Jsoup.parse(html)

    val title = parseTitle(document)
    val content = parseContent(document)

    if (title.isEmpty || content.isEmpty) {
      throw ParseException()
    }

    ParsedPage(title, content)
  }

  private def parseTitle(doc: Document): String =
    HtmlUtils.trim(doc.select(".b-content-body h1.b-article__title").text())

  private def parseContent(doc: Document): String = {
    val content = doc.select(".b-content-body .b-article__body")

    val strong = Option(content.select("strong"))
    val spam = Option(content.select("div.b-inject"))

    strong.foreach(element => element.remove())
    spam.foreach(element => element.remove())

    val article = if (content.text().startsWith(".")) {
      content.text().substring(1)
    } else {
      content.text()
    }

    HtmlUtils.trim(article)
  }
}
