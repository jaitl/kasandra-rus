package com.github.jaitl.kasandra

import com.github.jaitl.kasandra.models.ArticleNews
import com.jaitlapps.kasandra.configs.MySteamConfig
import com.jaitlapps.kasandra.services.MySteamService
import com.typesafe.config.ConfigFactory
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.{CountVectorizer, IDF}
import org.apache.spark.sql.SparkSession

object NewsClustering extends App {
  val spark = SparkSession
    .builder()
    .appName("Spark SQL Example")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  val rawNews = spark.read.json("/data/sitecrawl/news-100.json").as[ArticleNews]

  val mySteamConfig = MySteamConfig(ConfigFactory.load().getConfig("mysteam"))
  val mySteamService: MySteamService = new MySteamService(mySteamConfig)

  val newsData = rawNews.map(row => (row.id, mySteamService.steam(row.title).map(_.lex).filter(_.isDefined).map(_.get))).toDF("id", "content")

  val cvModel = new CountVectorizer()
    .setInputCol("content")
    .setOutputCol("rawFeatures")
    .fit(newsData)

  val vectorData = cvModel.transform(newsData).cache()

  val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
  val idfModel = idf.fit(vectorData)
  val rescaledData = idfModel.transform(vectorData)

  val numClusters = 100
  val kmeans = new KMeans().setK(numClusters)
  val model = kmeans.fit(rescaledData.select("features"))

  val prediction = model.transform(rescaledData).select("id", "content", "prediction")
  prediction.collect().foreach(println)

  val wssse = model.computeCost(rescaledData)
  println(s"wssse: ${wssse}")

  //model.clusterCenters

}
