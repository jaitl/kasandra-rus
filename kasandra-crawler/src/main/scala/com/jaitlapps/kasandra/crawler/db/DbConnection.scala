package com.jaitlapps.kasandra.crawler.db

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcProfile

class DbConnection {
  private val dc = DatabaseConfig.forConfig[JdbcProfile]("database")
  val profile: JdbcProfile = dc.profile

  val db: JdbcBackend#DatabaseDef = dc.db
}
