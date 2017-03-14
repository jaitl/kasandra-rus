package com.jaitlapps.kasandra.crawler.parsers.impl

import com.jaitlapps.kasandra.crawler.exceptions.ParseException
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

    if (title.isEmpty || annotation.isEmpty || content.isEmpty) {
      throw ParseException()
    }

    ParsedPage(title, annotation, content)
  }

  private def parseTitle(doc: Document) = doc.select("#contentBody h1.b-article__title").text().trim

  private def parseAnnotation(doc: Document) = {
    val ann = Option(doc.select("#contentBody .b-article__body p").first())

    if (ann.isDefined && !ann.get.text().trim.isEmpty) {
      ann.get.text().trim
    } else {
      val content = parseContent(doc)

      if (content.length > 500) {
        content.substring(0, 500)
      } else {
        content
      }
    }

  }

  private def parseContent(doc: Document) = {
    val content = doc.select("#contentBody .b-article__body") //.text().trim

    val spam = Option(content.select("div.b-inject__article"))

    if (spam.isDefined) {
      spam.get.remove()
    }

    content.text().trim
  }
}
