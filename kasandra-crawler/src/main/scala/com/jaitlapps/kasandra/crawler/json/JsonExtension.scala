package com.jaitlapps.kasandra.crawler.json

import java.sql.Timestamp
import java.util.UUID

import com.jaitlapps.kasandra.crawler.models.SiteType
import org.json4s.CustomSerializer
import org.json4s.JsonAST.JInt
import org.json4s.JsonAST.JLong
import org.json4s.JsonAST.JNull
import org.json4s.JsonAST.JString

trait JsonExtension {
  case object UUIDSerialiser extends CustomSerializer[UUID](format => (
    {
      case JString(s) => UUID.fromString(s)
      case JNull => null
    },
    {
      case x: UUID => JString(x.toString)
    }
  ))

  case object SiteTypeSerialiser extends CustomSerializer[SiteType](format => (
    {
      case JString(st) => SiteType(st)
    },
    {
      case st: SiteType => JString(st.name)
    }
  ))

  case object TimestampSerialiser extends CustomSerializer[Timestamp](format => (
    {
      case JInt(ts) => new Timestamp(ts.longValue())
    },
    {
      case ts: Timestamp => JInt(ts.toInstant.toEpochMilli)
    }
  ))
}
