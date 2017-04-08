package com.jaitlapps.kasandra.crawler.utils

object HtmlUtils {
  private val nbspSpace = "\\u00a0"
  def trim(str: String): String = str.replaceAll(nbspSpace, " ").trim
}
