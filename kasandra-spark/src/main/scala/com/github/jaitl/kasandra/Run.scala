package com.github.jaitl.kasandra

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.feature.{CountVectorizer, HashingTF, IDF, Tokenizer}
import org.apache.spark.sql.SparkSession

object Run extends App {

  val spark = SparkSession
    .builder()
    .appName("Spark SQL Example")
    .master("local[*]")
    .getOrCreate()

  val sentenceData = spark.createDataFrame(Seq(
    (1, "один два два"),
    (2, "один два два"),
    (3, "один два два"),
    (4, "один два два"),
    (5, "три три четыре пять шесть"),
    (6, "три три четыре пять шесть"),
    (7, "три три четыре пять шесть"),
    (8, "три три четыре пять шесть"),
    (9, "три три четыре пять шесть"),
    (10, "три три четыре пять шесть"),
    (11, "три три шесть шесть семь"),
    (12, "три три шесть шесть семь"),
    (13, "три три шесть шесть семь"),
    (14, "три три шесть шесть семь")
  )).toDF("label", "sentence")

  val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
  val wordsData = tokenizer.transform(sentenceData).cache()

  /* val hashingTF = new HashingTF()
    .setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(7)

  val featurizedData = hashingTF.transform(wordsData)
  // alternatively, CountVectorizer can also be used to get term frequency vectors

  val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
  val idfModel = idf.fit(featurizedData)
  val rescaledData = idfModel.transform(featurizedData)

  rescaledData.select("features", "label").take(3).foreach(println)*/


  val cvModel = new CountVectorizer()
    .setInputCol("words")
    .setOutputCol("rawFeatures")
    .fit(wordsData)

  val vectorData = cvModel.transform(wordsData)

  val idf2 = new IDF().setInputCol("rawFeatures").setOutputCol("features")
  val idfModel2 = idf2.fit(vectorData)
  val rescaledData2 = idfModel2.transform(vectorData)

  rescaledData2.select("features", "label").take(3).foreach(println)


  val numClusters = 2
  val numIterations = 20
  val kmeans = new KMeans().setK(3)
  val model = kmeans.fit(rescaledData2.select("features"))

  model.transform(rescaledData2).select("label", "prediction").show()

  model.clusterCenters



  /*val wssse = model.computeCost(rescaledData2)
  println(s"wssse: ${wssse}")*/

}
