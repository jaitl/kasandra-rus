package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.parser.SiteParser
import org.jsoup.nodes.Document

object VestiSiteParser extends SiteParser {
  override protected def parseTitle(doc: Document): String =
    doc.select(".article .article__title").text()

  override protected def parseContent(doc: Document): String =
    doc.select(".article .article__text .js-mediator-article").text()
}
