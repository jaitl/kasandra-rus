package com.jaitlapps.kasandra.downloader.savers

trait Saver {
  def save(html: String): Unit
}
