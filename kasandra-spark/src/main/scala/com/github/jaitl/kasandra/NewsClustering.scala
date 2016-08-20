package com.github.jaitl.kasandra

import com.github.jaitl.kasandra.models.ArticleNews
import org.apache.spark.{SparkConf, SparkContext}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.read

// spark-submit --class "com.github.jaitl.kasandra.NewsClustering" --master local[4] /Users/jaitl/dev/kasandra-rus/kasandra-spark/build/libs/kasandra-spark-1.0-all.jar

object NewsClustering extends App {
  val conf = new SparkConf().setMaster("local[4]").setAppName("My App")
  val sc = new SparkContext(conf)

  val news = sc.textFile("/data/sitecrawl/news-4965.json").map(line => {
    implicit val formats = DefaultFormats
    read[ArticleNews](line)
  })

  news.collect().map(a => a.title).foreach(println)
}
