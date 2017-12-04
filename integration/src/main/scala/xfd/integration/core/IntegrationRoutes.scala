package xfd.integration.core

import scala.concurrent.{ExecutionContext, Future}

import akka.Done
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import xfd.integration.in.jenkins.JenkinsModule
import xfd.integration.out.KaaFlow

object IntegrationRoutes {

  def runIntegrationRoutes()(
      implicit
      system: ActorSystem,
      materializer: ActorMaterializer,
      executionContext: ExecutionContext
  ): Future[Done] = {
    val jenkinsSource = JenkinsModule.integrationSource

    jenkinsSource
      .via(KaaFlow())
      .runWith(Sink.foreach(println))
  }
}
