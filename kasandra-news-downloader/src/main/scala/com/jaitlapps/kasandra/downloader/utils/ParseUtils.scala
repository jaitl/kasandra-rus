package com.jaitlapps.kasandra.downloader.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaitlapps.kasandra.downloader.models.NewsSite.NewsSite
import com.jaitlapps.kasandra.downloader.models.RawPost
import org.jsoup.select.Elements

import scala.collection.JavaConverters._

object ParseUtils {

  private val regex = "(https?)://[^\\s/$.?#].[^\\s]*".r

  def parseUrls(text: String): List[String] = {
    regex.findAllIn(text).toList
  }

  def parseVkJson(json:String, site: NewsSite) : List[RawPost] = {
    val mapper = new ObjectMapper()
    val tree = mapper.readTree(json)
    val nodes = tree.get("response").get("items").elements().asScala.toList

    nodes.map(jNode => {
      val id = jNode.get("id").asText()
      val date = jNode.get("date").asLong()
      val post = jNode.get("text").asText()

      val urls = parseUrls(post)

      RawPost(id, date, post, urls, site)
    })
  }

  def deleteElement(element: Elements, selector: String): Unit = {
    element.select(selector).remove()
  }

  def deleteElementByText(element: Elements, text: String*): Unit = {
    element.select("p").iterator().asScala.toList.foreach(el => {
      text.foreach(t => {
        if (el.text().startsWith(t)) {
          el.remove()
        }
      })
    })
  }
}
