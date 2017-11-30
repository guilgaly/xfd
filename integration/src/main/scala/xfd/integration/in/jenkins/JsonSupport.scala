package xfd.integration.in.jenkins

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, NullOptions, RootJsonFormat}
import xfd.integration.in.jenkins.model._

trait JsonSupport extends SprayJsonSupport {

  private val jsonProtocol = new DefaultJsonProtocol with NullOptions
  import jsonProtocol._

  implicit val buildFormat: RootJsonFormat[Build] =
    jsonFormat3(Build)
  implicit val queueItemFormat: RootJsonFormat[QueueItem] =
    jsonFormat12(QueueItem)
  implicit val jobFormat: RootJsonFormat[Job] =
    jsonFormat3(Job)
  implicit val jobWithDetailsFormat: RootJsonFormat[JobWithDetails] =
    jsonFormat20(JobWithDetails)
}
