package com.jaitlapps.kasandra.crawler.models

abstract sealed class SiteType(val name: String)

object SiteType {
  def apply(name: String) : SiteType = name match {
    case RtSite.name => RtSite
    case RiaSite.name => RiaSite
  }

  case object RtSite extends SiteType("RT")
  case object RiaSite extends SiteType("RIA")
}
