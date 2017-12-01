package xfd.integration.in.jenkins.model

private[jenkins] case class Build(
    number: Int,
    queueId: Option[Int],
    url: String,
)
