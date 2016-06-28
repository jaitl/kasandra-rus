package com.jaitlapps.kasandra.services

import com.jaitlapps.kasandra.configs.MySteamConfig
import com.jaitlapps.kasandra.models.{NewsDocVector, NewsDocument, Token}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FunSuite, Matchers, WordSpec}

class TfIdfServiceTest extends WordSpec with Matchers {
  val mySteamConfig = MySteamConfig(ConfigFactory.load().getConfig("mysteam"))
  val mySteamService = new MySteamService(mySteamConfig)
  val stopWordsService = new StopWordsService
  val tokenizeService = new TokenizerService(mySteamService, stopWordsService)

  "tf" should {

    val tfIdfService = new TfIdfService(tokenizeService)

    "correct compute, 1" in {
      val doc = new NewsDocument("1", "заголовок", "два два")
      val tf = tfIdfService.computeTf(Token("два", "два"), doc)

      tf shouldEqual 1
    }

    "correct compute, 2/3" in {
      val doc = new NewsDocument("1", "заголовок", "два два три")
      val tf = tfIdfService.computeTf(Token("два", "два"), doc)

      tf shouldEqual 0.6666 +- 0.001
    }

    "correct compute, 1/3" in {
      val doc = new NewsDocument("1", "заголовок", "два три три")
      val tf = tfIdfService.computeTf(Token("два", "два"), doc)

      tf shouldEqual 0.3333 +- 0.001
    }

    "correct compute, 0" in {
      val doc = new NewsDocument("1", "заголовок", "три три три")
      val tf = tfIdfService.computeTf(Token("два", "два"), doc)

      tf shouldEqual 0
    }
  }

  "idf" should {
    "correct compute, 1" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "два два один три")

      service.fill(doc1 :: Nil)

      val idf = service.computeIdf(Token("два", "два"))

      idf shouldEqual 0
    }

    "correct compute, 2/1" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "два два три четыре")
      val doc2 = new NewsDocument("1", "заголовок", "один один один один")

      service.fill(doc1 :: Nil)
      service.fill(doc2 :: Nil)

      val idf = service.computeIdf(Token("два", "два"))

      idf shouldEqual 0.693 +- 0.001
    }

    "correct compute, 3/1" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "два два три четыре")
      val doc2 = new NewsDocument("1", "заголовок", "пять пять пять один")
      val doc3 = new NewsDocument("1", "заголовок", "семь семь девять семь")

      service.fill(doc1 :: doc2 :: Nil)
      service.fill(doc3 :: Nil)

      val idf = service.computeIdf(Token("два", "два"))

      idf shouldEqual 1.0986 +- 0.001
    }

    "correct compute, 5/2" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "два два три четыре")
      val doc2 = new NewsDocument("1", "заголовок", "пять пять пять один")
      val doc3 = new NewsDocument("1", "заголовок", "семь семь девять семь")
      val doc4 = new NewsDocument("1", "заголовок", "два два три пять")
      val doc5 = new NewsDocument("1", "заголовок", "семь семь девять семь")

      service.fill(doc1 :: doc2 :: Nil)
      service.fill(doc3 :: Nil)
      service.fill(doc4 :: doc5 :: Nil)

      val idf = service.computeIdf(Token("два", "два"))

      idf shouldEqual 0.9162 +- 0.0001
    }

    "correct compute, 0" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "три три четыре пять")
      val doc2 = new NewsDocument("1", "заголовок", "три три пять шесть")
      val doc3 = new NewsDocument("1", "заголовок", "три три шесть шесть семь")

      service.fill(doc1 :: doc2 :: doc3 :: Nil)

      val idf = service.computeIdf(Token("два", "два"))

      idf shouldEqual 0
    }
  }

  "tf-idf" should {
    "correct compute, 1" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "два два четыре пять")
      val doc2 = new NewsDocument("1", "заголовок", "три три пять шесть")
      val doc3 = new NewsDocument("1", "заголовок", "три три шесть шесть семь")

      service.fill(doc1 :: doc2 :: doc3 :: Nil)

      val tfidf = service.computeTfIdf(Token("два", "два"), doc1)

      tfidf shouldEqual 0.5493 +- 0.0001
    }

    "correct compute, 2" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "два два четыре пять")
      val doc2 = new NewsDocument("1", "заголовок", "три три пять шесть")
      val doc3 = new NewsDocument("1", "заголовок", "три три шесть шесть семь")

      service.fill(doc1 :: doc2 :: doc3 :: Nil)

      val tfidf = service.computeTfIdf(Token("два", "два"), doc2)

      tfidf shouldEqual 0
    }

    "correct compute, 3" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "два два")
      val doc2 = new NewsDocument("1", "заголовок", "три три пять шесть")
      val doc3 = new NewsDocument("1", "заголовок", "три три шесть шесть семь")

      service.fill(doc1 :: doc2 :: doc3 :: Nil)

      val tfidf = service.computeTfIdf(Token("два", "два"), doc1)

      tfidf shouldEqual 1.0986 +- 0.0001
    }
  }

  "vector names" should {
    "best names" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "два два")
      val doc2 = new NewsDocument("1", "заголовок", "три три пять шесть")
      val doc3 = new NewsDocument("1", "заголовок", "три три шесть шесть семь")

      service.fill(doc1 :: doc2 :: doc3 :: Nil)

      service.vectorNames() shouldBe Seq("два", "три", "пять", "шесть", "семь")
    }
  }

  "vector" should {
    "work right" in {
      val service = new TfIdfService(tokenizeService)

      val doc1 = new NewsDocument("1", "заголовок", "один два два")
      val doc2 = new NewsDocument("2", "заголовок", "три три четыре пять шесть")
      val doc3 = new NewsDocument("3", "заголовок", "три три шесть шесть семь")

      service.fill(doc1 :: doc2 :: doc3 :: Nil)

      service.vectorNames() shouldBe Seq("один", "два", "три", "четыре", "пять", "шесть", "семь")

      service.computeTfIdfVector(doc1) shouldBe NewsDocVector("1", Seq(0.3662040962227032, 0.7324081924454064, 0, 0, 0, 0, 0))
      service.computeTfIdfVector(doc2) shouldBe NewsDocVector("2", Seq(0.0, 0.0, 0.16218604324326577, 0.21972245773362198, 0.21972245773362198, 0.08109302162163289, 0))
      service.computeTfIdfVector(doc3) shouldBe NewsDocVector("3", Seq(0.0, 0.0, 0.16218604324326577, 0.0, 0.0, 0.16218604324326577, 0.21972245773362198))
    }
  }
}
