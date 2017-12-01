package xfd.integration.in.jenkins.model

import spray.json.JsObject

private[jenkins]case class QueueItem(
  actions: Seq[JsObject], // TODO QueueItemActions
  blocked: Boolean,
  buildable: Boolean,
  id: Long,
  inQueueSince: Long,
  params: String,
  stuck: Boolean,
  task: JsObject, // TODO QueueTask
  url: String,
  why: String,
  cancelled: Boolean,
  executable: JsObject, // TODO Executable
)