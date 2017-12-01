package xfd.integration.in.jenkins.model

import spray.json.JsObject

private[jenkins] case class BuildWithDetails(
    actions: Seq[JsObject],
    building: Option[Boolean],
    description: Option[String],
    displayName: String,
    duration: Option[Long],
    estimatedDuration: Option[Long],
    fullDisplayName: String,
    id: Option[String],
    timestamp: Option[Long],
    result: Option[BuildResult],
    artifacts: Option[Seq[JsObject]],
    consoleOutputText: Option[String],
    consoleOutputHtml: Option[String],
    changeSet: Option[JsObject],
    builtOn: Option[String],
    culprits: Option[Seq[JsObject]],
)
