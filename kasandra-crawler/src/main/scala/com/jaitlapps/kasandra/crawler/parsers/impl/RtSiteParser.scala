package com.jaitlapps.kasandra.crawler.parsers.impl

import com.jaitlapps.kasandra.crawler.exceptions.ParseException
import com.jaitlapps.kasandra.crawler.models.ParsedPage
import com.jaitlapps.kasandra.crawler.parsers.SiteParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object RtSiteParser extends SiteParser {
  override def parse(html: String): ParsedPage = {
    val document = Jsoup.parse(html)

    val title = parseTitle(document)
    val annotation = parseAnnotation(document)
    val content = parseContent(document)

    if (title.isEmpty || annotation.isEmpty || content.isEmpty) {
      throw ParseException()
    }

    ParsedPage(title, annotation, content)
  }

  private def parseTitle(doc: Document) = doc.select(".article h1.article__heading").text().trim

  private def parseAnnotation(doc: Document) = doc.select(".article .article__summary").text().trim

  private def parseContent(doc: Document) = doc.select(".article .article__text").text().trim
}
