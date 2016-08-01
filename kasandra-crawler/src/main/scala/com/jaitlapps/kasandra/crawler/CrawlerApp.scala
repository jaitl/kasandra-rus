package com.jaitlapps.kasandra.crawler

import akka.actor.ActorSystem
import com.jaitlapps.kasandra.crawler.actors.CrawlControllerActor.StartCrawling
import com.jaitlapps.kasandra.crawler.actors.{CrawlControllerActor, VkWallCrawlerActor}
import com.jaitlapps.kasandra.crawler.config.CrawlConfig
import com.typesafe.config.ConfigFactory

object CrawlerApp extends App {
  val system = ActorSystem("kasandraCrawlSystem")
  val conf = ConfigFactory.load()

  val crawlConf = CrawlConfig(conf.getConfig("crawl-config"))

  val vkWallCrawler = system.actorOf(VkWallCrawlerActor.props())
  val controllerActor = system.actorOf(CrawlControllerActor.props(crawlConf, vkWallCrawler))

  controllerActor ! StartCrawling
}
