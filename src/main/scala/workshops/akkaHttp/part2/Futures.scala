package workshops.akkaHttp.part2

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, StatusCodes}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import workshops.Utils.performRequestWithLogging

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Futures {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher


    val route = path("route1" ){
      get {
        parameters('mode.as[Int]){ (mode: Int) =>
          onComplete(getStringInTheFuture(mode)){
            // remember to match on scala.util success/failure
            case Success(str) => complete(str)
            case Failure(e : CustomException) => complete(StatusCodes.NotFound)
            case Failure(_) => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }



    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route1?mode=1"))
    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route1?mode=2"))
    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route1?mode=3"))
    // show what happens if we don't match


    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  def getStringInTheFuture(mode: Int): Future[String] = mode match {
    case 1 => Future.successful("result")
    case 2 => Future.failed(new CustomException)
    case _ => Future.failed(new Exception("exception"))
  }

  class CustomException extends Exception
}
