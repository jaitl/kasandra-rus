package com.jaitlapps.kasandra.crawler.article.parser

trait SiteParser {
  def parse(html: String): ParsedPage
}

case class ParsedPage (title: String, annotation: String, content: String)
