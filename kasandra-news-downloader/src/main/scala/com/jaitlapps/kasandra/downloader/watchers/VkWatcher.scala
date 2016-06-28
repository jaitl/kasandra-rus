package com.jaitlapps.kasandra.downloader.watchers

import com.jaitlapps.kasandra.downloader.models.NewsSite.NewsSite
import com.jaitlapps.kasandra.downloader.models.RawPost
import com.jaitlapps.kasandra.downloader.utils.ParseUtils

import scalaj.http.Http

class VkWatcher extends Watcher {
  private val urlApi = "https://api.vk.com/method"
  private val methodName = "wall.get"
  private val params = Map("v" -> "5.50")

  override def postEntries(site: NewsSite, count: Int): List[RawPost] = {
    val response = Http(urlApi + "/" + methodName)
      .params(params)
      .param("domain", site.vk.get)
      .param("count", count.toString)
      .asString

    if (response.isSuccess)
      ParseUtils.parseVkJson(response.body, site)
    else //TODO logs!!
      List()
  }
}

