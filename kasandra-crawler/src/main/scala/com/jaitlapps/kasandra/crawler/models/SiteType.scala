package com.jaitlapps.kasandra.crawler.models

abstract sealed class SiteType(val name: String)

object SiteType {
  val list: Set[SiteType] = Set(
    RtSite, RiaSite, LifeSite, OneTvSite, LentaSite, RbcSite, KpSite, VestiSite
  )


  def apply(name: String) : SiteType = name match {
    case RtSite.name => RtSite
    case RiaSite.name => RiaSite
    case LifeSite.name => LifeSite
    case OneTvSite.name => OneTvSite
    case LentaSite.name => LentaSite
    case RbcSite.name => RbcSite
    case KpSite.name => KpSite
    case VestiSite.name => VestiSite
  }

  case object RtSite extends SiteType("RT")
  case object RiaSite extends SiteType("RIA")
  case object LifeSite extends SiteType("LIFE")
  case object OneTvSite extends SiteType("1TV")
  case object LentaSite extends SiteType("LENTA")
  case object RbcSite extends SiteType("RBC")
  case object KpSite extends SiteType("KP")
  case object VestiSite extends SiteType("VESTI")
}
