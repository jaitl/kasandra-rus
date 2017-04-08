package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.parser.SiteParser
import org.jsoup.nodes.Document

object KpSiteParser extends SiteParser {
  override protected def parseTitle(doc: Document): String =
  doc.select("article header h1").text()

  override protected def parseContent(doc: Document): String =
    doc.select("article .text").text()
}
