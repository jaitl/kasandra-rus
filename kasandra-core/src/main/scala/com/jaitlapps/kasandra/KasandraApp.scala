package com.jaitlapps.kasandra

import ru.stachek66.nlp.mystem.holding.Request

object KasandraApp extends App {
  val result = MyStemSingleton
    .mystemAnalyzer
    .analyze(Request("Мама мыла раму."))

  val dsf = ""
}
