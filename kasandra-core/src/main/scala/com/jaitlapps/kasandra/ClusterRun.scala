package com.jaitlapps.kasandra

import com.aliasi.cluster.KMeansClusterer
import com.jaitlapps.kasandra.configs.MySteamConfig
import com.jaitlapps.kasandra.lingpipe.extractors.TdIdfFeatureExtractor
import com.jaitlapps.kasandra.models.NewsDocument
import com.jaitlapps.kasandra.services.{StopWordsService, MySteamService, TfIdfService, TokenizerService}
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._

object ClusterRun extends App {
  val mySteamConfig = MySteamConfig(ConfigFactory.load().getConfig("mysteam"))
  val mySteamService: MySteamService = new MySteamService(mySteamConfig)
  val stopWordsService = new StopWordsService()
  val tokenizer = new TokenizerService(mySteamService, stopWordsService)
  val tfIdfService = new TfIdfService(tokenizer)
  val tdIdfExtractor = new TdIdfFeatureExtractor(tokenizer, tfIdfService)

  val numClusters = 2
  val numMaxEpochs = 2
  val cluster = new KMeansClusterer[NewsDocument](tdIdfExtractor, numClusters, numMaxEpochs, false, 0)

  cluster.cluster(Set[NewsDocument]())
}
