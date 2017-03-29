package com.jaitlapps.kasandra.crawler.article.parser

import com.jaitlapps.kasandra.crawler.article.parser.impl.RiaSiteParser
import com.jaitlapps.kasandra.crawler.article.parser.impl.RtSiteParser
import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.SiteType

object ParserFactory {
  def getParser(site: CrawlSite): SiteParser = site.siteType match {
    case SiteType.RtSite => RtSiteParser
    case SiteType.RiaSite => RiaSiteParser
  }
}
