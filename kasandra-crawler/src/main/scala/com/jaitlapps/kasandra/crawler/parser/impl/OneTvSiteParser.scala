package com.jaitlapps.kasandra.crawler.parser.impl

import com.jaitlapps.kasandra.crawler.parser.SiteParser
import org.jsoup.nodes.Document

object OneTvSiteParser extends SiteParser {

  override protected def parseTitle(doc: Document): String = doc.select("article.itv-news h1.title").text()

  override protected def parseContent(doc: Document): String = {
    val content = doc.select("article.itv-news .text-block")

    content.text()
  }
}
