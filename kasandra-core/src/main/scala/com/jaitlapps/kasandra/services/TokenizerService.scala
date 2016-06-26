package com.jaitlapps.kasandra.services

import com.jaitlapps.kasandra.models.{Token, NewsDocument}
import ru.stachek66.nlp.mystem.holding.Request

class TokenizerService(mySteamService: MySteamService, stopWordsService: StopWordsService) {
  def tokenize(doc: NewsDocument): Seq[Token] = {
    mySteamService.mystemAnalyzer.analyze(Request(doc.text))
      .info.filter(i => i.lex.isDefined)
      .filter(i => !stopWordsService.contains(i.lex.get))
      .map(i => Token(i.initial, i.lex.get)).toSeq
  }
}
