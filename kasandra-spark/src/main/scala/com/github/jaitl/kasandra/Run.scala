package com.github.jaitl.kasandra

import org.apache.spark.{SparkConf, SparkContext}

// spark-submit --class "com.github.jaitl.kasandra.Run" --master local[4] /Users/jaitl/dev/kasandra-rus/kasandra-spark/build/libs/kasandra-spark-1.0.jar
object Run extends App {
  val conf = new SparkConf().setMaster("local").setAppName("My App")
  val sc = new SparkContext(conf)

  val input = sc.textFile("/data/test_data/words.txt")

  val words = input.flatMap(line => line.split(" "))

  val counts = words.map(word => (word, 1)).reduceByKey((a, b) => a + b)

  counts.map{ case (s, c) => s"word: $s, count: $c" }.collect().foreach(println)
}
