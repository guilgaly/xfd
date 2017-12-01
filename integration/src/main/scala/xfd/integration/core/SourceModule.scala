package xfd.integration.core

import scala.concurrent.ExecutionContext

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import xfd.integration.core.model.ProjectStatus

trait SourceModule {

  def integrationSource(
      implicit
      system: ActorSystem,
      materializer: ActorMaterializer,
      executionContext: ExecutionContext
  ): Source[ProjectStatus, Cancellable]
}
