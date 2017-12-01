package xfd.integration.in.jenkins

import akka.NotUsed
import akka.stream.scaladsl._
import xfd.integration.core.model.{BuildStatus, CiStatus, ProjectStatus}
import xfd.integration.in.jenkins.model.{
  BuildResult,
  BuildWithDetails,
  JobWithDetails
}

private[jenkins] object JenkinsConversionFlow {

  def apply(): Flow[(JobWithDetails, Option[BuildWithDetails]),
                    ProjectStatus,
                    NotUsed] = {

    Flow.fromFunction {
      case (job, Some(build)) =>
        val buildStatus =
          build.result match {
            case Some(BuildResult.SUCCESS) => BuildStatus.Success
            case Some(BuildResult.FAILURE) => BuildStatus.Failed
            case _                         => BuildStatus.Unknown
          }
        ProjectStatus(job.name, CiStatus(buildStatus))
      case (job, _) => ProjectStatus(job.name, CiStatus(BuildStatus.Unknown))
    }
  }
}
