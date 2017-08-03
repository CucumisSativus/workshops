package workshops.akkaHttp.part2

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Multipart.FormData
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, Uri}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import workshops.Utils.performRequestWithLogging

object Parameters {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route = path("route1" ){
      get {
        parameters('color, 'count.as[Int]){ (color: String, count: Int) =>
          complete(s"color - $color count - $count")
        }
      }
    } ~ path("route2") {
      parameters('color ?, 'count.as[Int] ? 0){ (color: Option[String], count: Int) =>
        get {
          complete(s"color - $color count $count")
        } ~
        post {
          complete(s"color - $color count $count")
        }

      }
    }



    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route1?color=c&count=3"))

    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route1?color=c"))

    println(performRequestWithLogging(HttpMethods.GET, "http://localhost:8080/route2?color=c&count=3"))
    val query = Query(Map("color" -> "red", "count" -> "2"))
    println(performRequestWithLogging(HttpRequest(HttpMethods.POST, Uri("http://localhost:8080/route2").withQuery(query))))



    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
