package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.exceptions.ParseException
import com.jaitlapps.kasandra.crawler.parser.ParsedPage
import com.jaitlapps.kasandra.crawler.parser.SiteParser
import com.jaitlapps.kasandra.crawler.utils.HtmlUtils
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

  private def parseTitle(doc: Document) = HtmlUtils.trim(doc.select(".article h1.article__heading").text())

  private def parseContent(doc: Document) = {
    val article = doc.select(".article")

    val summary = Option(article.select(".article__summary")).map(e => HtmlUtils.trim(e.text()))
    val text = HtmlUtils.trim(article.select(".article__text").text())

    summary.map(s => s"$s $text").getOrElse(text)
  }
}
