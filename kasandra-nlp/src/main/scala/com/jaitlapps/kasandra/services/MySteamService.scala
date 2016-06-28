package com.jaitlapps.kasandra.services

import java.io.File

import com.jaitlapps.kasandra.configs.MySteamConfig
import ru.stachek66.nlp.mystem.holding.{Request, Response, Factory}
import ru.stachek66.nlp.mystem.model.Info

class MySteamService(config: MySteamConfig) {
  private val mystemAnalyzer = new Factory(config.arguments)
    .newMyStem(config.version, Option(new File(config.path))).get

  def steam(text: String): Seq[Info] = {
    mystemAnalyzer.analyze(Request(text)).info.toSeq
  }
}
