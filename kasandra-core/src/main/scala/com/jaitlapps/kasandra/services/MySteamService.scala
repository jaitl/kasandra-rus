package com.jaitlapps.kasandra.services

import java.io.File

import com.jaitlapps.kasandra.configs.MySteamConfig
import ru.stachek66.nlp.mystem.holding.Factory

class MySteamService(config: MySteamConfig) {
  val mystemAnalyzer = new Factory(config.arguments).newMyStem(config.version, Option(new File(config.path))).get
}
