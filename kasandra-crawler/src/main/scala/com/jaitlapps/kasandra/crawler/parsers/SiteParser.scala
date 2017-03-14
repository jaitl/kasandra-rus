package com.jaitlapps.kasandra.crawler.parsers

import com.jaitlapps.kasandra.crawler.models.ParsedPage

trait SiteParser {
  def parse(html: String): ParsedPage
}
