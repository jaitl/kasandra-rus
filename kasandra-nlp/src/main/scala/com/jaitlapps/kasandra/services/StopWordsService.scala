package com.jaitlapps.kasandra.services

import scala.io.Source

class StopWordsService {
  val stopSet: Set[String] = Source.fromURL(getClass.getResource("/stopwords.txt")).getLines().toTraversable.toSet

  def contains(word: String): Boolean = stopSet.contains(word)
}
