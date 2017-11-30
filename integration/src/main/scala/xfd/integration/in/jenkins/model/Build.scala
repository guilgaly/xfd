package xfd.integration.in.jenkins.model

case class Build(
    number: Int,
    queueId: Option[Int],
    url: String,
)
