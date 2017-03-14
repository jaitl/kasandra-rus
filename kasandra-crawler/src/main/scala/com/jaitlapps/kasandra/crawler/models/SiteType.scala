package com.jaitlapps.kasandra.crawler.models

trait SiteType {
  val name: String
}

object SiteType {
  def apply(name: String) : SiteType = name match {
    case RtSite.name => RtSite
    case RiaSite.name => RiaSite
  }

  case object RtSite extends SiteType {
    val name = "RT"
  }

  case object RiaSite extends SiteType {
    val name = "RIA"
  }
}
