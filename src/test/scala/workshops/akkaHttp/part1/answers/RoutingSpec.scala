package workshops.akkaHttp.part1.answers

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import workshops.UnitSpec
import workshops.akkaHttp.part1.answers.RoutingSpec.VisitorsCountController

class RoutingSpec extends UnitSpec with ScalatestRouteTest{
  "A visitor count controller" when {
    "user ask for current visitors count" should {
      "return proper number" in {
        val controller = new VisitorsCountController
        Get("/visitors/count") ~> controller.route ~> check {
          assert(status.isSuccess())
          responseAs[String] mustBe "0"
        }
      }
    }
    "new visitor arrives" should {
      "increase the visitors count value" in {
        val controller = new VisitorsCountController
        Post("/visitors") ~> controller.route ~> check{
          assert(status.isSuccess())
          responseAs[String] mustBe "ok"
        }
        controller.visitorsCount mustBe 1
      }
    }
  }
}

object RoutingSpec{
  class VisitorsCountController{
    var visitorsCount = 0
    def route: Route =
      pathPrefix("visitors") {
        pathSuffix("count") {
          get {
            complete(visitorsCount.toString)
          }
        } ~
          post {
            visitorsCount += 1
            complete("ok")
          }
      }
  }
}