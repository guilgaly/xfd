package xfd.integration.core

import scala.concurrent.{ExecutionContext, Future}

import akka.Done
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import xfd.integration.in.jenkins.JenkinsSource

object IntegrationRoutes {

  def runIntegrationRoutes()(
      implicit
      system: ActorSystem,
      materializer: ActorMaterializer,
      executionContext: ExecutionContext
  ): Future[Done] = {
    val jenkinsSource = JenkinsSource()

    jenkinsSource.runWith(Sink.foreach(println))
  }
}
