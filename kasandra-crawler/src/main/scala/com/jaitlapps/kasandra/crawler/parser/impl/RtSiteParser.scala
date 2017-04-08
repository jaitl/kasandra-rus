package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.parser.SiteParser
import com.jaitlapps.kasandra.crawler.utils.HtmlUtils
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

object RtSiteParser extends SiteParser {
  protected override def parseTitle(doc: Document): String = doc.select(".article h1.article__heading").text()

  protected override def parseContent(doc: Document): String = {
    val article = doc.select(".article")

    val opinion = article.select("p em")

    if (!opinion.isEmpty) {
      opinion.last().remove()
    }

    val summary = article.select(".article__summary") match {
      case el: Elements if el.isEmpty => None
      case el: Elements => Some(HtmlUtils.trim(el.text()))
    }

    val text = HtmlUtils.trim(article.select(".article__text").text())

    summary.map(s => s"$s $text").getOrElse(text)
  }
}
