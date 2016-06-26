package com.jaitlapps.kasandra.configs

import com.typesafe.config.Config

case class MySteamConfig(path: String, arguments: String, version: String)

object MySteamConfig {
  def apply(config: Config): MySteamConfig = MySteamConfig(path = config.getString("path"),
                                                            arguments = config.getString("arguments"),
                                                            version = config.getString("version"))
}