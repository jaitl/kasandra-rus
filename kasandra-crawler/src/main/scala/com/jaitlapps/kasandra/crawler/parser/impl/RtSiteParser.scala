package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.exceptions.ParseException
import com.jaitlapps.kasandra.crawler.parser.ParsedPage
import com.jaitlapps.kasandra.crawler.parser.SiteParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object RtSiteParser extends SiteParser {
  override def parse(html: String): ParsedPage = {
    val document = Jsoup.parse(html)

    val title = parseTitle(document)
    val content = parseContent(document)

    if (title.isEmpty || content.isEmpty) {
      throw ParseException()
    }

    ParsedPage(title, content)
  }

  private def parseTitle(doc: Document) = doc.select(".article h1.article__heading").text().trim

  private def parseContent(doc: Document) = doc.select(".article .article__text").text().trim
}
