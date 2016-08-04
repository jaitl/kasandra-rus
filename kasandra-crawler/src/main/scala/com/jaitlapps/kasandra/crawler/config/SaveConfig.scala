package com.jaitlapps.kasandra.crawler.config

import com.typesafe.config.Config

case class SaveConfig(path: String)

object SaveConfig {
  def apply(conf: Config): SaveConfig = SaveConfig(conf.getString("path"))
}
