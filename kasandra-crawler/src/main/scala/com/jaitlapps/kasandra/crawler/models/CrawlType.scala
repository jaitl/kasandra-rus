package com.jaitlapps.kasandra.crawler.models

abstract sealed class CrawlType(val name: String)

object CrawlType {
  def apply(name: String): CrawlType = name match {
    case Wall.name => Wall
    case Site.name => Site
  }

  case object Wall extends CrawlType("Wall")
  case object Site extends CrawlType("Site")
}
