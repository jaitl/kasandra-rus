package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.parser.SiteParser
import com.jaitlapps.kasandra.crawler.utils.HtmlUtils
import org.jsoup.nodes.Document

object LifeSiteParser extends SiteParser {
  override protected def parseTitle(doc: Document): String =
    doc.select("article header h1").text()

  override protected def parseContent(doc: Document): String = {
    val article = doc.select("article .post-page-content")

    val subtitle = article.select(".post-page-subtitle")

    val content = article.select(".content-note")

    val div = content.select("div")

    if (!div.isEmpty) {
      div.remove()
    }

    if (!subtitle.isEmpty) {
      HtmlUtils.trim(subtitle.text()) + " " + HtmlUtils.trim(content.text())
    } else {
      content.text()
    }
  }
}
