package com.jaitlapps.kasandra.services

import com.jaitlapps.kasandra.models.{Token, NewsDocument}

class TokenizerService(mySteamService: MySteamService, stopWordsService: StopWordsService) {
  def tokenize(doc: NewsDocument): Seq[Token] = {
    mySteamService.steam(doc.text)
      .filter(i => i.lex.isDefined)
      .filter(i => !stopWordsService.contains(i.lex.get))
      .map(i => Token(i.initial, i.lex.get)).toSeq
  }
}
