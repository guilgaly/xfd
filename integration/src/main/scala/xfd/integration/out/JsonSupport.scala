package xfd.integration.out

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import xfd.integration.core.model.{BuildStatus, CiStatus, ProjectStatus}
import xfd.integration.util.JsonUtils

private[out] trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val buildStatusFormat: JsonFormat[BuildStatus] =
    JsonUtils.enumFormat(BuildStatus)
  implicit val ciStatusFormat: RootJsonFormat[CiStatus] =
    jsonFormat1(CiStatus)

  implicit val projectStatusFormat: RootJsonFormat[ProjectStatus] =
    jsonFormat2(ProjectStatus)
}
