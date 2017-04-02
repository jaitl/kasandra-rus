package com.jaitlapps.kasandra.crawler.exceptions

case class BadUrlException(message: String, code: Int) extends Exception(message)
