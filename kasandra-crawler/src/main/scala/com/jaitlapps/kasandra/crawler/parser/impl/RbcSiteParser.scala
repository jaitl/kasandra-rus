package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.parser.SiteParser
import com.jaitlapps.kasandra.crawler.utils.HtmlUtils
import org.jsoup.nodes.Document

object RbcSiteParser extends SiteParser {
  override protected def parseTitle(doc: Document): String =
    doc.select(".article .article__header__title").text()

  override protected def parseContent(doc: Document): String = {
    val article = doc.select(".article__content .article__text")

    val overview = HtmlUtils.trim(article.select(".article__text__overview").text())

    val div = article.select("div")

    if (!div.isEmpty) {
      div.remove()
    }

    if (!overview.isEmpty) {
      val text = HtmlUtils.trim(article.text())
      if (text.isEmpty) {
        overview
      } else {
        overview + " " + text
      }
    } else {
      article.text()
    }
  }
}
