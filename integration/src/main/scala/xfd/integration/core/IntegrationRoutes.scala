package xfd.integration.core

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream._
import akka.stream.scaladsl._
import spray.json.DefaultJsonProtocol._
import spray.json.JsObject

trait IntegrationRoutes extends JsonSupport {

  // we leave these abstract, since they will be provided by the App
  implicit protected def system: ActorSystem
  implicit protected def materializer: ActorMaterializer
  implicit protected def executionContext: ExecutionContext

  private val authorizationHeader =
    headers.Authorization(
      BasicHttpCredentials(
        "guillaume.galy@fabernovel.com",
        "769f853ad29bf447ddd15bab13b6ad5e"
      )
    )
  private val pollRequest =
    HttpRequest(
      uri = Uri("https://jenkins.znx.fr/job/bnp-welcome/api/json"),
      headers = immutable.Seq(authorizationHeader)
    )

  private val jenkinsSource = Source
    .tick(0.seconds, 5.seconds, ())
    .map(_ => println("tick!"))
    .mapAsync(1)(_ => Http().singleRequest(pollRequest))
    .map { httpResponse =>
      println(s"httpResponse: $httpResponse")
      httpResponse
    }
    .mapAsync(1) {
      case HttpResponse(StatusCodes.OK, _, entity, _) =>
        Unmarshal(entity).to[JsObject].map(Some(_))
      case _ =>
        Future.successful(None)
    }
    .map { parsedResponse =>
      println(s"parsedResponse: $parsedResponse")
      parsedResponse
    }
    .collect { case Some(jsObject) => jsObject }

  def runIntegrationRoutes(): Future[Done] =
    jenkinsSource.runWith(Sink.foreach(println))

}
