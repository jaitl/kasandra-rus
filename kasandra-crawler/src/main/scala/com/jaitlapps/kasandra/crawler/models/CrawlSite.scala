package com.jaitlapps.kasandra.crawler.models

import java.util.UUID

case class CrawlSite(id: UUID, siteType: SiteType, domain: String, vkGroup: String)

