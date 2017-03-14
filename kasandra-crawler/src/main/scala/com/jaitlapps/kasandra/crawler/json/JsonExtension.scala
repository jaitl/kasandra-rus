package com.jaitlapps.kasandra.crawler.json

import java.util.UUID

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

trait JsonExtension {
  case object UUIDSerialiser extends CustomSerializer[UUID](format => (
    {
      case JString(s) => UUID.fromString(s)
      case JNull => null
    },
    {
      case x: UUID => JString(x.toString)
    }
    )
  )
}
