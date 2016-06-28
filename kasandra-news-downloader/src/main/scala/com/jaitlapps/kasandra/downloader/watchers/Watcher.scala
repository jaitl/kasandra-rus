package com.jaitlapps.kasandra.downloader.watchers

import com.jaitlapps.kasandra.downloader.models.NewsSite.NewsSite
import com.jaitlapps.kasandra.downloader.models.RawPost

trait Watcher {
  def postEntries(site: NewsSite, count: Int): List[RawPost]
}

