package com.jaitlapps.kasandra.crawler.parsers

import com.jaitlapps.kasandra.crawler.models.{CrawlSite, SiteType}
import com.jaitlapps.kasandra.crawler.parsers.impl.{RiaSiteParser, RtSiteParser}

object ParserFactory {
  def getParser(site: CrawlSite): SiteParser = site.siteType match {
    case SiteType.RtSite => RtSiteParser
    case SiteType.RiaSite => RiaSiteParser
  }
}
