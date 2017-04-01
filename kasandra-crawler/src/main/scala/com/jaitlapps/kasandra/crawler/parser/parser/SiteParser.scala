package com.jaitlapps.kasandra.crawler.parser.parser

trait SiteParser {
  def parse(html: String): ParsedPage
}

case class ParsedPage (title: String, annotation: String, content: String)
