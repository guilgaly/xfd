package xfd.integration

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import xfd.integration.admin.AdminRoutes
import xfd.integration.core.IntegrationRoutes

/**
  * Main class
  */
object QuickstartServer extends App with AdminRoutes {

  // set up ActorSystem and other dependencies here
  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val log = Logging(system, classOf[AdminRoutes])

//  private val userRegistryActor: ActorRef =
//    system.actorOf(UserRegistryActor.props, "userRegistryActor")

  // from the UserRoutes trait
  lazy val routes: Route = adminRoutes
  val serverBindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(routes, "localhost", 8080)

  // integration routes
  IntegrationRoutes.runIntegrationRoutes()

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  StdIn.readLine()

  serverBindingFuture
    .flatMap(_.unbind())
    .onComplete { done =>
      done.failed.map { ex =>
        log.error(ex, "Failed unbinding")
      }
      system.terminate()
    }
}
