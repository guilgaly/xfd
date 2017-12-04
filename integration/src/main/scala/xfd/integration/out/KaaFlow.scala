package xfd.integration.out

import scala.collection.immutable
import scala.concurrent.ExecutionContext

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString
import xfd.integration.core.model.{BuildStatus, ProjectStatus}

object KaaFlow {

  def apply()(
      implicit
      system: ActorSystem,
      materializer: ActorMaterializer,
      executionContext: ExecutionContext
  ): Flow[ProjectStatus, ProjectStatus, NotUsed] = {

    val kaaSettings = KaaSettings(system.settings.config)

    val authorizationHeader =
      headers.Authorization(
        BasicHttpCredentials(
          kaaSettings.username,
          kaaSettings.password
        )
      )

    Flow[ProjectStatus].mapAsync(1) { projectStatus =>
      val status =
        if (projectStatus.ci.masterBuild == BuildStatus.Success) "success"
        else "fail"

      val formData = Multipart
        .FormData(
          Multipart.FormData.BodyPart.Strict(
            name = "notification",
            entity = HttpEntity.Strict(
              contentType = ContentTypes.`application/json`,
              data = ByteString(
                """{"applicationId":"1","schemaId":"7","topicId":"1","type":"USER"}"""))
          ),
          Multipart.FormData.BodyPart.Strict(
            "file",
            HttpEntity(
              ContentTypes.`application/json`,
              s"""{"status":"$status","stability":{"null":null},"progress":{"null":null}}"""),
            Map("filename" -> "notification.json")
          )

          /*Multipart.FormData.BodyPart.Strict(
            name = "file",
            entity = HttpEntity.Strict(
              contentType = ContentTypes.`application/octet-stream`,
              data = ByteString(
                s"""{"status":"$status","stability":{"null":null},"progress":{"null":null}}""")
            )
          )*/
        )
        .toEntity()

      val request = HttpRequest(
        uri = Uri(kaaSettings.notificationUrl),
        headers = immutable.Seq(authorizationHeader),
        method = HttpMethods.POST,
        entity = formData
      )
      Http().singleRequest(request).map { x =>
        println(x)
        projectStatus
      }
    }

  }
}
