package com.jaitlapps.kasandra.crawler.parser.parser

import com.jaitlapps.kasandra.crawler.models.CrawlSite
import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.parser.parser.impl.RiaSiteParser
import com.jaitlapps.kasandra.crawler.parser.parser.impl.RtSiteParser

object ParserFactory {
  def getParser(site: CrawlSite): SiteParser = site.siteType match {
    case SiteType.RtSite => RtSiteParser
    case SiteType.RiaSite => RiaSiteParser
  }
}
