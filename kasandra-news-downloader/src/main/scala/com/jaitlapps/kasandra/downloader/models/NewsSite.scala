package com.jaitlapps.kasandra.downloader.models

sealed case class NewsSiteValue(name:String, vk:Option[String], url:String)

object NewsSite extends Enumeration {
  type NewsSite = NewsSiteValue

  val RussiaTodaySite = NewsSiteValue("RT", Some("rt_russian"), "russian.rt.com")
  val RiaNewsSite = NewsSiteValue("RIA", Some("ria"), "ria.ru")
}

