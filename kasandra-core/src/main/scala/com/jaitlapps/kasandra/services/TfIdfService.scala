package com.jaitlapps.kasandra.services

import com.jaitlapps.kasandra.models.{Token, NewsDocument}

class TfIdfService(tokenizerService: TokenizerService) {
  var docCount = 0
  val idfMap = scala.collection.mutable.HashMap[String, Int]()

  def fill(documents: List[NewsDocument]): Unit = {

    docCount += documents.size

    val docsWord = documents
      .map(d => tokenizerService.tokenize(d).map(_.lexeme).toSet)
      .foreach(docs => docs.foreach(lex => {
        if (idfMap.contains(lex)) {
          idfMap.put(lex, idfMap.get(lex).get + 1)
        } else {
          idfMap.put(lex, 1)
        }
      }))
  }

  def computeTf(token: Token, document: NewsDocument): Double = {
    val tokens = tokenizerService.tokenize(document)
    val docMap = tokens.groupBy(_.lexeme).map{case (lex, seq) => lex -> seq.size}

    docMap.get(token.lexeme).map(c => c.toDouble/tokens.size.toDouble).getOrElse(0)
  }

  def computeIdf(token: Token): Double = {
    idfMap.get(token.lexeme).map(c => docCount.toDouble/c.toDouble).map(n => Math.log(n)).getOrElse(0.0)
  }

  def computeTfIdf(token: Token, document: NewsDocument): Double = computeTf(token, document) * computeIdf(token)
}
