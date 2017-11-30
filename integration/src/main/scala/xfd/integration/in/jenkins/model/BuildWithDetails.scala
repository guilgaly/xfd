package xfd.integration.in.jenkins.model

import spray.json.JsObject

case class BuildWithDetails(
    actions: Seq[JsObject],
    building: Boolean,
    description: String,
    displayName: String,
    duration: Long,
    estimatedDuration: Long,
    fullDisplayName: String,
    id: String,
    timestamp: Long,
    result: BuildResult,
    artifacts: Seq[JsObject],
    consoleOutputText: Option[String],
    consoleOutputHtml: Option[String],
    changeSet: JsObject,
    builtOn: String,
    culprits: Seq[JsObject],
)
