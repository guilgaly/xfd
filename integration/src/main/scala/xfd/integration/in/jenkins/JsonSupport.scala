package xfd.integration.in.jenkins

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import xfd.integration.in.jenkins.model._

trait JsonSupport extends SprayJsonSupport {

  private val jsonProtocol = new DefaultJsonProtocol with NullOptions
  import jsonProtocol._

  implicit val buildResultFormat: JsonFormat[BuildResult] =
    new JsonFormat[BuildResult] {
      override def read(json: JsValue): BuildResult = json match {
        case JsString(str) =>
          BuildResult.namesToValuesMap
            .getOrElse(str, deserializationError("Invalid BuildResult"))
        case _ => deserializationError("BuildResult should be a string")
      }

      override def write(obj: BuildResult): JsValue = JsString(obj.toString)
    }
  implicit val buildFormat: RootJsonFormat[Build] =
    jsonFormat3(Build)
  implicit val queueItemFormat: RootJsonFormat[QueueItem] =
    jsonFormat12(QueueItem)
  implicit val jobFormat: RootJsonFormat[Job] =
    jsonFormat3(Job)
  implicit val jobWithDetailsFormat: RootJsonFormat[JobWithDetails] =
    jsonFormat20(JobWithDetails)
  implicit val buildWithDetailsFormat: RootJsonFormat[BuildWithDetails] =
    jsonFormat16(BuildWithDetails)

}
