package xfd.integration.in.jenkins.model

case class JobWithDetails(
    name: String,
    url: String,
    fullName: String,
    description: String,
    displayName: String,
    buildable: Boolean,
    builds: Seq[Build], // Build
    firstBuild: Option[Build], // Build
    lastBuild: Option[Build], // Build
    lastCompletedBuild: Option[Build], // Build
    lastFailedBuild: Option[Build], // Build
    lastStableBuild: Option[Build], // Build
    lastSuccessfulBuild: Option[Build], // Build
    lastUnstableBuild: Option[Build], // Build
    lastUnsuccessfulBuild: Option[Build], // Build
    nextBuildNumber: Int,
    inQueue: Boolean,
    queueItem: Option[QueueItem], // QueueItem
    downstreamProjects: Seq[Job], // Job
    upstreamProjects: Seq[Job], // Job
)
