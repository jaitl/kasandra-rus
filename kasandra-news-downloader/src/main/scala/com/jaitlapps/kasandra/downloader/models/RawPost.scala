package com.jaitlapps.kasandra.downloader.models

import com.jaitlapps.kasandra.downloader.models.NewsSite.NewsSite

case class RawPost(id: String, date: Long, post: String, urls: List[String], site: NewsSite)

