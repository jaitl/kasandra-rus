package com.jaitlapps.kasandra.crawler.parser

import com.jaitlapps.kasandra.crawler.models.SiteType
import com.jaitlapps.kasandra.crawler.parser.impl.KpSiteParser
import com.jaitlapps.kasandra.crawler.parser.impl.LentaSiteParser
import com.jaitlapps.kasandra.crawler.parser.impl.OneTvSiteParser
import com.jaitlapps.kasandra.crawler.parser.impl.RbcSiteParser
import com.jaitlapps.kasandra.crawler.parser.impl.RiaSiteParser
import com.jaitlapps.kasandra.crawler.parser.impl.RtSiteParser

object ParserFactory {
  def getParser(site: SiteType): SiteParser = site match {
    case SiteType.RtSite => RtSiteParser
    case SiteType.RiaSite => RiaSiteParser
    case SiteType.OneTvSite => OneTvSiteParser
    case SiteType.LentaSite => LentaSiteParser
    case SiteType.RbcSite => RbcSiteParser
    case SiteType.KpSite => KpSiteParser
  }
}
