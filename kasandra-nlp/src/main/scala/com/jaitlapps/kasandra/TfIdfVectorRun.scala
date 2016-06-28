package com.jaitlapps.kasandra

import com.jaitlapps.kasandra.configs.MySteamConfig
import com.jaitlapps.kasandra.models.NewsDocument
import com.jaitlapps.kasandra.services.{MySteamService, StopWordsService, TfIdfService, TokenizerService}
import com.typesafe.config.ConfigFactory


object TfIdfVectorRun extends App {
  val mySteamConfig = MySteamConfig(ConfigFactory.load().getConfig("mysteam"))
  val mySteamService: MySteamService = new MySteamService(mySteamConfig)
  val stopWordsService = new StopWordsService()
  val tokenizer = new TokenizerService(mySteamService, stopWordsService)
  val tfIdfService = new TfIdfService(tokenizer)

  val doc1 = NewsDocument("1", "Мост", "На мосту совершалось преступление.")
  val doc2 = NewsDocument("2", "Преступление", "Иван Васильевич видел, как на мосту совершалось первое преступление")
  val doc3 = NewsDocument("3", "Рама", "Дом, улица, фанарь, Иван.")

  tfIdfService.fill(doc1 :: doc2 :: doc3 :: Nil)

  println("Steam doc1: ")
  mySteamService.steam(doc1.text).foreach(i => println(s"token: '${i.initial}', lexeme: '${i.lex.get}', grammar: [${i.rawResponse}]"))
  println("")
  println("Steam doc2: ")
  mySteamService.steam(doc2.text).foreach(i => println(s"token: '${i.initial}', lexeme: '${i.lex.get}', grammar: [${i.rawResponse}]"))
  println("")
  println("Steam doc3: ")
  mySteamService.steam(doc3.text).foreach(i => println(s"token: '${i.initial}', lexeme: '${i.lex.get}', grammar: [${i.rawResponse}]"))

  println("")

  println("vector names: " + tfIdfService.vectorNames().mkString(", "))
  println("doc1 vector: " + tfIdfService.computeTfIdfVector(doc1).vector.mkString(", "))
  println("doc2 vector: " + tfIdfService.computeTfIdfVector(doc2).vector.mkString(", "))
  println("doc3 vector: " + tfIdfService.computeTfIdfVector(doc3).vector.mkString(", "))
}
