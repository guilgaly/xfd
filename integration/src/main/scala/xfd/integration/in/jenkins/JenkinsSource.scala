package xfd.integration.in.jenkins

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success
import scala.util.control.NonFatal

import akka.actor.{ActorSystem, Cancellable}
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream._
import akka.stream.scaladsl._
import xfd.integration.in.jenkins.model.{BuildWithDetails, JobWithDetails}

private[jenkins] object JenkinsSource extends JsonSupport {

  def apply(jenkinsSettings: JenkinsSettings)(
      implicit
      system: ActorSystem,
      materializer: ActorMaterializer,
      executionContext: ExecutionContext
  ): Source[(JobWithDetails, Option[BuildWithDetails]), Cancellable] = {

    val authorizationHeader =
      headers.Authorization(
        BasicHttpCredentials(
          jenkinsSettings.username,
          jenkinsSettings.password
        )
      )

    def request(uri: String) =
      HttpRequest(
        uri = Uri(s"${jenkinsSettings.rootUrl}/$uri"),
        headers = immutable.Seq(authorizationHeader)
      )

    val pollRequest = request(s"${jenkinsSettings.jobPath}/api/json")

    def buildRequest(number: Int) =
      request(s"${jenkinsSettings.jobPath}/$number/api/json")

    Source
      .tick(0.seconds, jenkinsSettings.interval, ())
      .map(_ => println("tick!"))
      .mapAsync(1)(_ => Http().singleRequest(pollRequest))
//      .map { httpResponse =>
//        println(s"httpResponse: $httpResponse")
//        httpResponse
//      }
      .mapAsync(1) {
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          Unmarshal(entity)
            .to[JobWithDetails]
            .recover {
              case NonFatal(t) =>
                println("JobWithDetails parsing error")
                t.printStackTrace()
                println(
                  s"JSON: ${entity.toStrict(5.seconds).map(_.data.utf8String)}")
                throw t
            }
            .transform {
              case Success(job) => Success(Some(job))
              case _            => Success(None)
            }

        //.map(Some(_))
        case _ =>
          Future.successful(None)
      }
//      .map { parsedResponse =>
//        println(s"parsedResponse: $parsedResponse")
//        parsedResponse
//      }
      .collect { case Some(job) => job }
      .mapAsync(1) { job =>
        job.lastCompletedBuild match {
          case Some(build) =>
            val request = buildRequest(build.number)
            println(s"fetching ${request.uri}")
            Http()
              .singleRequest(request)
              .map(build => (job, Some(build)))
          case _ => Future.successful((job, None))
        }
      }
      .mapAsync(1) {
        case (job, Some(HttpResponse(StatusCodes.OK, _, entity, _))) =>
          Unmarshal(entity)
            .to[BuildWithDetails]
            .recover {
              case NonFatal(t) =>
                println("BuildWithDetails parsing error : ")
                t.printStackTrace()
                println(
                  s"JSON: ${entity.toStrict(5.seconds).map(_.data.utf8String)}")
                throw t
            }
            .map(build => (job, Some(build)))
        case (job, _) =>
          Future.successful((job, None))
      }
  }

}
