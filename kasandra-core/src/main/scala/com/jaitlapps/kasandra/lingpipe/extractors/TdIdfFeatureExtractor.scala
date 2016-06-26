package com.jaitlapps.kasandra.lingpipe.extractors

import java.util

import com.aliasi.util.FeatureExtractor
import com.jaitlapps.kasandra.models.NewsDocument
import com.jaitlapps.kasandra.services.{TfIdfService, TokenizerService}

import scala.collection.JavaConverters._

class TdIdfFeatureExtractorFull(val tokenizer: TokenizerService, val tdIdfService: TfIdfService) extends FeatureExtractor[NewsDocument] {

  override def features(doc: NewsDocument): util.Map[String, java.lang.Double] = {
    val tokens = tokenizer.tokenize(doc)

    val findMap = tokens.map(t => t.lexeme -> double2Double(tdIdfService.computeTfIdf(t, doc))).toMap
    findMap.asJava

    val zeroMap = tdIdfService.idfMap.keySet.diff(findMap.keySet).map(t => t -> double2Double(0.0)).toMap

    (findMap ++ zeroMap).asJava
  }
}

class TdIdfFeatureExtractor(val tokenizer: TokenizerService, val tdIdfService: TfIdfService) extends FeatureExtractor[NewsDocument] {

  override def features(doc: NewsDocument): util.Map[String, java.lang.Double] = {
    val tokens = tokenizer.tokenize(doc)

    tokens.map(t => t.lexeme -> double2Double(tdIdfService.computeTfIdf(t, doc))).toMap.asJava
  }
}