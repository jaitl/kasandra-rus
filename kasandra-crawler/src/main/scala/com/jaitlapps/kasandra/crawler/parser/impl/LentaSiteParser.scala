package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.parser.SiteParser
import org.jsoup.nodes.Document

object LentaSiteParser extends SiteParser {
  override protected def parseTitle(doc: Document): String =
    doc.select(".b-topic__content .b-topic__title").text()

  override protected def parseContent(doc: Document): String =
    doc.select(".b-topic__content .b-text").text()
}
