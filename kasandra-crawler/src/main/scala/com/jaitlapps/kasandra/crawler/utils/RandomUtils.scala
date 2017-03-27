package com.jaitlapps.kasandra.crawler.utils

object RandomUtils {
  def range(from: Int, to: Int): Int = {
    val rnd = new scala.util.Random
    from + rnd.nextInt((to - from) + 1)
  }
}
