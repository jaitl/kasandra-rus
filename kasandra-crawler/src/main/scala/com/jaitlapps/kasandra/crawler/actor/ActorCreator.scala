package com.jaitlapps.kasandra.crawler.actor

import akka.actor.ActorRef
import akka.actor.ActorRefFactory

trait ActorCreator[T] {
  def create(factory: ActorRefFactory, name: String): T => ActorRef
}
