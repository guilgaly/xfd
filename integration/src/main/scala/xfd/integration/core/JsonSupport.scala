package xfd.integration.core

import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import xfd.integration.core.model.{BuildStatus, CiStatus, ProjectStatus}
import xfd.integration.util.JsonUtils

private[core] trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val buildStatusFormat: JsonFormat[BuildStatus] =
    JsonUtils.enumFormat(BuildStatus)
  implicit val ciStatusFormat: RootJsonFormat[CiStatus] =
    jsonFormat1(CiStatus)

  implicit val projectStatusFormat: RootJsonFormat[ProjectStatus] =
    jsonFormat2(ProjectStatus)
}
