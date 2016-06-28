package com.jaitlapps.kasandra

import java.util

import com.aliasi.cluster.KMeansClusterer
import com.aliasi.util.FeatureExtractor
import com.jaitlapps.kasandra.configs.MySteamConfig
import com.jaitlapps.kasandra.models.NewsDocument
import com.jaitlapps.kasandra.parser.JsonNewsParser
import com.jaitlapps.kasandra.services.{MySteamService, StopWordsService, TfIdfService, TokenizerService}
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._
import scala.io.Source

object LingPipeKmeansRun extends App {
  val mySteamConfig = MySteamConfig(ConfigFactory.load().getConfig("mysteam"))
  val mySteamService: MySteamService = new MySteamService(mySteamConfig)
  val stopWordsService = new StopWordsService()
  val tokenizer = new TokenizerService(mySteamService, stopWordsService)
  val tfIdfService = new TfIdfService(tokenizer)
  val tdIdfExtractor = new TdIdfFeatureExtractorVector(tokenizer, tfIdfService)

  val newsJson = Source.fromURL(getClass.getResource("/data/news.json")).mkString
  val news = JsonNewsParser.parseNews(newsJson)

  tfIdfService.fill(news)

  val numClusters = 3
  val numMaxEpochs = 0
  val cluster = new KMeansClusterer[NewsDocument](tdIdfExtractor, numClusters, numMaxEpochs, true, 0.1)

  val result = cluster.cluster(news.toSet.asJava).asScala

  println(s"count clusters: ${result.size}")

  var counter = 1
  for(set <- result) {
    val ids = set.asScala.map(_.id).mkString(", ")
    println(s"cluster №$counter, ids: [$ids]")
    counter += 1
  }
}

class TdIdfFeatureExtractor(val tokenizer: TokenizerService, val tdIdfService: TfIdfService) extends FeatureExtractor[NewsDocument] {

  override def features(doc: NewsDocument): util.Map[String, java.lang.Double] = {
    val tokens = tokenizer.tokenize(doc)

    tokens.map(t => t.lexeme -> double2Double(tdIdfService.computeTfIdf(t, doc))).toMap.asJava
  }
}

class TdIdfFeatureExtractorVector(val tokenizer: TokenizerService, val tdIdfService: TfIdfService) extends FeatureExtractor[NewsDocument] {

  override def features(doc: NewsDocument): util.Map[String, java.lang.Double] = {

    val vector = tdIdfService.computeTfIdfVector(doc).vector
    val names = tdIdfService.vectorNames()

    vector.zip(names).map{case (v, n) => n -> double2Double(v)}.toMap.asJava
  }
}