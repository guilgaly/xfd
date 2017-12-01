package xfd.integration.in.jenkins

import scala.concurrent.ExecutionContext

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import xfd.integration.core.SourceModule
import xfd.integration.core.model.ProjectStatus

object JenkinsModule extends SourceModule {

  override def integrationSource(
      implicit
      system: ActorSystem,
      materializer: ActorMaterializer,
      executionContext: ExecutionContext
  ): Source[ProjectStatus, Cancellable] = {
    val jenkinsSettings = JenkinsSettings(system.settings.config)

    val source = JenkinsSource(jenkinsSettings)
    val conversion = JenkinsConversionFlow()

    source.via(conversion)
  }
}
