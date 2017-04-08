package com.jaitlapps.kasandra.crawler.utils

object HtmlUtils {
  private val nbspSpace = "\\u00a0"
  private val zeroWidthSpace = "\\u200B"
  def trim(str: String): String = str.replaceAll(nbspSpace, " ").replaceAll(zeroWidthSpace, "").trim
}
