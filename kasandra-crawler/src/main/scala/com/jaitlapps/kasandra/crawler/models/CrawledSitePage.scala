package com.jaitlapps.kasandra.crawler.models

import java.util.UUID

case class CrawledSitePage(id: UUID, date: Long, title: String, annotation: String, content: String, url: String)
