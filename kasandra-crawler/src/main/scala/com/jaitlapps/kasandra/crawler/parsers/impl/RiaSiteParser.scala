package com.jaitlapps.kasandra.crawler.parsers.impl

import com.jaitlapps.kasandra.crawler.models.ParsedPage
import com.jaitlapps.kasandra.crawler.parsers.SiteParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object RiaSiteParser extends SiteParser {
  override def parse(html: String): ParsedPage = {
    val document = Jsoup.parse(html)

    val title = parseTitle(document)
    val annotation = parseAnnotation(document)
    val content = parseContent(document)

    ParsedPage(title, annotation, content)
  }

  private def parseTitle(doc: Document) = doc.select("#contentBody h1.b-article__title").text().trim

  private def parseAnnotation(doc: Document) = doc.select("#contentBody .b-article__body p").first().text().trim

  private def parseContent(doc: Document) = doc.select("#contentBody .b-article__body").text().trim
}
