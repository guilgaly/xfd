package xfd.integration

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import xfd.integration.admin.AdminRoutes

class AdminRoutesSpec
    extends WordSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest
    with AdminRoutes {

  lazy val routes = adminRoutes

  "adminRoutes" when {
    "asked for health status" should {
      "return 'up'" in {
        val request = HttpRequest(uri = "/health")

        request ~> routes ~> check {
          status shouldBe StatusCodes.OK
          contentType shouldBe ContentTypes.`application/json`
          entityAs[String] shouldBe """{"status":"up"}"""
        }
      }
    }
  }
}
