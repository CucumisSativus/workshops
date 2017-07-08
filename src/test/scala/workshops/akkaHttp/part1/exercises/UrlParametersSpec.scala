package workshops.akkaHttp.part1.exercises
import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.testkit.GraphStageMessages.Failure
import workshops.UnitSpec

import scala.util.Try

class UrlParametersSpec extends UnitSpec with ScalatestRouteTest{
  "A padlocks controller" should {
    "add new padlock" in {
      val expectedPadlock = Padlock("angelina", "brad")
      val controller = new PadlocksController()
      Post("/padlock/new/angelina/brad") ~> controller.route ~> check(
        status mustBe StatusCodes.OK
      )

      controller.padlocks mustBe List(expectedPadlock)
    }

    "read a padlock which exist in the database" in {
      val expectedPadlock = Padlock("angelina", "brad")
      val controller = new PadlocksController(Array(expectedPadlock))
      Get("/padlock/1") ~> controller.route ~> check(
        responseAs[String] mustBe "angelina + brad = WNM"
      )
    }

    "return 404 if padlock does not exist" in {
      val controller = new PadlocksController()
      Get("/padlock/4") ~> controller.route ~> check(
        status mustBe StatusCodes.NotFound
      )
    }
  }
}

case class Padlock(person1Name: String, person2Name: String)

class PadlocksController(initialList: Array[Padlock] = Array()){
  var padlocks: Array[Padlock] = initialList

  val route : Route =  ???

  private def serializePadlock(p: Padlock) = {
    s"${p.person1Name} + ${p.person2Name} = WNM"
  }
}