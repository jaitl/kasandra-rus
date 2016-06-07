package com.jaitlapps.kasandra

import java.io.File

import ru.stachek66.nlp.mystem.holding.{Factory, Request}

object MyStemSingleton {
  val mystemAnalyzer = new Factory("-igd --eng-gr --format json --weight")
    .newMyStem("3.0", Option(new File("/data/mystem/mystem"))).get

}