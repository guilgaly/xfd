package xfd.integration.util

import enumeratum.{Enum, EnumEntry}
import spray.json.{JsString, JsValue, JsonFormat, deserializationError}

object JsonUtils {

  def enumFormat[T <: EnumEntry](enum: Enum[T]): JsonFormat[T] =
    new JsonFormat[T] {
      override def read(json: JsValue): T = json match {
        case JsString(str) =>
          enum
            .withNameOption(str)
            .getOrElse(deserializationError("Invalid BuildResult"))
        case _ => deserializationError("BuildResult should be a string")
      }
      override def write(obj: T): JsValue = JsString(obj.toString)
    }
}
