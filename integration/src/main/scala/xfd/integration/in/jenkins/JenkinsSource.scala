package xfd.integration.in.jenkins

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

import akka.actor.{ActorSystem, Cancellable}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream._
import akka.stream.scaladsl._
import xfd.integration.in.jenkins.model.JobWithDetails

object JenkinsSource extends JsonSupport {

  def apply()(
      implicit
      system: ActorSystem,
      materializer: ActorMaterializer,
      executionContext: ExecutionContext
  ): Source[JobWithDetails, Cancellable] = {

    val jenkinsSettings = JenkinsSettings(system.settings.config)

    val authorizationHeader =
      headers.Authorization(
        BasicHttpCredentials(
          jenkinsSettings.username,
          jenkinsSettings.password
        )
      )

    val pollRequest =
      HttpRequest(
        uri = Uri("https://jenkins.znx.fr/job/bnp-welcome/api/json"),
        headers = immutable.Seq(authorizationHeader)
      )

    Source
      .tick(0.seconds, jenkinsSettings.interval, ())
      .map(_ => println("tick!"))
      .mapAsync(1)(_ => Http().singleRequest(pollRequest))
      .map { httpResponse =>
        println(s"httpResponse: $httpResponse")
        httpResponse
      }
      .mapAsync(1) {
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          Unmarshal(entity)
            .to[JobWithDetails]
            .recover {
              case NonFatal(t) =>
                println("Error : " + t)
                throw t
            }
            .map(Some(_))
        case _ =>
          Future.successful(None)
      }
      .map { parsedResponse =>
        println(s"parsedResponse: $parsedResponse")
        parsedResponse
      }
      .collect { case Some(jsObject) => jsObject }
  }

}
