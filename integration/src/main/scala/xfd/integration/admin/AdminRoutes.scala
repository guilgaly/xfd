package xfd.integration.admin

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete

trait AdminRoutes extends JsonSupport {

  // we leave these abstract, since they will be provided by the App
  implicit protected def system: ActorSystem

  lazy val adminRoutes: Route =
    pathPrefix("health") {
      concat(
        pathEnd {
          concat(
            get {
              complete(Ok("up"))
            }
          )
        }
      )
    }
}
