package com.jaitlapps.kasandra.parser

import com.jaitlapps.kasandra.models.NewsDocument
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.jackson.JsonMethods._

object JsonNewsParser {
  implicit val formats = DefaultFormats

  def parseNews(json: String): Seq[NewsDocument] = {
    parse(json).extract[News].news
  }

  case class News(news: Seq[NewsDocument])
}
