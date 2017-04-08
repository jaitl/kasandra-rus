package com.jaitlapps.kasandra.crawler.parser

import com.jaitlapps.kasandra.crawler.exceptions.ParseException
import com.jaitlapps.kasandra.crawler.utils.HtmlUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

abstract class SiteParser {
  def parse(html: String): ParsedPage = {
    val document = Jsoup.parse(html)

    val title = HtmlUtils.trim(parseTitle(document))
    val content = HtmlUtils.trim(parseContent(document))

    if (title.isEmpty || content.isEmpty) {
      throw ParseException()
    }

    ParsedPage(title, content)
  }

  protected def parseTitle(doc: Document): String
  protected def parseContent(doc: Document): String
}

case class ParsedPage (title: String, content: String)
