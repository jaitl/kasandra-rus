package com.jaitlapps.kasandra.crawler.crawlers

import scalaj.http.Http

object VkWallCrawler {
  private val urlApi = "https://api.vk.com/method"
  private val methodName = "wall.get"
  private val params = Map("v" -> "5.53")

  def crawlWall(domain: String, offset: Int, count: Int): Option[String] = {
    val response = Http(urlApi + "/" + methodName)
      .params(params)
      .param("domain", domain)
      .param("count", count.toString)
      .param("offset", offset.toString)
      .asString

    if (response.isSuccess) {
      Some(response.body)
    } else {
      None
    }
  }
}