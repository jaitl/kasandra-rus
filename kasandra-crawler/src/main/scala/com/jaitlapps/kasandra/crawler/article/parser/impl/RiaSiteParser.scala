package com.jaitlapps.kasandra.crawler.article.parser.impl

import com.jaitlapps.kasandra.crawler.article.parser.ParsedPage
import com.jaitlapps.kasandra.crawler.article.parser.SiteParser
import com.jaitlapps.kasandra.crawler.exceptions.ParseException
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

  private def parseTitle(doc: Document) = doc.select(".b-content-body h1.b-article__title").text().trim

  private def parseAnnotation(doc: Document) = {
    val ann = Option(doc.select(".b-content-body .b-article__body p strong").parents().first())

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
    val content = doc.select(".b-content-body .b-article__body") //.text().trim

    val spam = Option(content.select("div.b-inject"))

    if (spam.isDefined) {
      spam.get.remove()
    }

    content.text().trim
  }
}
