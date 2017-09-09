package workshops.akkaHttp.part3

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Multipart.FormData
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import spray.json.DefaultJsonProtocol._
import spray.json._
import workshops.Utils.performRequestWithLogging

import scala.concurrent.Future

object JsonInRoutes {
  // import all crazy implicits
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    implicit val myRandomClassFormat = jsonFormat1(MyRandomClass)
    implicit val otherClassFormat = jsonFormat1(OtherClass)

    val buildClass: ((MyRandomClass) => Unit) => Unit = (f: MyRandomClass => Unit) => {
      f(MyRandomClass("builded from function"))
    }

    val changeFieldValue: (MyRandomClass) => OtherClass = (c: MyRandomClass) => {
      OtherClass(Vector(c))
    }

    def doSomeStuffWhichMightFail(param: String) = {
      if (param == "ok") Future.successful(MyRandomClass("field"))
      else if(param == "gomba") Future.failed(new IllegalArgumentException("We do not accept hungarian mushrooms"))
      else Future.failed(new Exception("(╯°□°）╯︵ ┻━┻"))
    }

    val route = path("route1") {
      get {
        complete(MyRandomClass("my field1"))
      } ~
        post {
          entity(as[MyRandomClass]) { c =>
            println(s"received class ${c}")
            complete(StatusCodes.NoContent)
          }
        }
    } ~ path("route2") {
      get {
        completeWith(instanceOf[MyRandomClass])(buildClass)
      }
    } ~ path("route3") {
      post {
        handleWith(changeFieldValue)
      }
    } ~ path("route4") {
      parameters('param) { param =>
        get {
          completeOrRecoverWith(doSomeStuffWhichMightFail(param)) {
            case c: IllegalArgumentException => complete(StatusCodes.BadRequest, c.getMessage)
            case ex => failWith(ex)
          }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route1"))

    println(performRequestWithLogging(
      HttpMethods.POST,
      "http://localhost:8080/route1",
      HttpEntity(ContentTypes.`application/json`, MyRandomClass("on post").toJson.compactPrint)
    ))


    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route2"))
    println(performRequestWithLogging(
      HttpMethods.POST,
      "http://localhost:8080/route3",
      HttpEntity(ContentTypes.`application/json`, MyRandomClass("for handle with").toJson.compactPrint)
    ))

    println(performRequestWithLogging(
      HttpMethods.GET,
      "http://localhost:8080/route4?param=ok"
    ))

    println(performRequestWithLogging(
      HttpMethods.GET,
      "http://localhost:8080/route4?param=gomba"
    ))

    println(performRequestWithLogging(
      HttpMethods.GET,
      "http://localhost:8080/route4?param=notOk"
    ))

    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  case class MyRandomClass(field1: String)

  case class OtherClass(myCLasses: Vector[MyRandomClass])

}
