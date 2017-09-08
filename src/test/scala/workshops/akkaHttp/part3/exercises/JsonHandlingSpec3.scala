package workshops.akkaHttp.part3.exercises

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.testkit.ScalatestRouteTest
import workshops.UnitSpec
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import workshops.akkaHttp.part3.exercises.JsonHandlingSpec3.{CheckoutController, ShoppingCart}
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

class JsonHandlingSpec3 extends UnitSpec with ScalatestRouteTest{
  import JsonHandlingSpec3._
  "Checkout controller" should {
    "initialize new shopping cart" in {
      val expectedId = "myId"
      val controller = new CheckoutController(() => expectedId)
      val expectedJson =
        s"""
          |{
          | "id": "$expectedId",
          |  "items": []
          |}
        """.stripMargin.parseJson
      Get("/checkout/new") ~> controller.route ~> check {
        responseAs[String].parseJson mustBe expectedJson
      }
      controller.carts(expectedId) mustBe ShoppingCart(Vector())
    }

    "add new item to cart" in {
      val newItem = ShopItem("item", BigDecimal(123))
      val cartId = "myId"
      val controller = new CheckoutController(initialCarts = Map(cartId -> ShoppingCart()))
      val expectedJson =
        s"""
           |{
           |  "id": "$cartId",
           |  "items": [
           |    {
           |      "name": "item",
           |      "price": 123
           |    }
           |  ]
           |}
         """.stripMargin.parseJson
      Post("/checkout/add").withEntity(ContentTypes.`application/json`, AddItemToCart(cartId, newItem).toJson.compactPrint) ~>
        controller.route ~> check{
          responseAs[String].parseJson mustBe expectedJson
      }

      controller.carts(cartId) mustBe ShoppingCart(Vector(newItem))
    }
  }

}

object JsonHandlingSpec3{
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import spray.json.DefaultJsonProtocol._

  case class ShopItem(name: String, price: BigDecimal)
  case class AddItemToCart(cartId: String, item: ShopItem)
  case class ShoppingCart(items: Vector[ShopItem] = Vector())
  implicit val itemFormat = jsonFormat2(ShopItem)
  implicit val addToCartFormat = jsonFormat2(AddItemToCart)
  implicit val cartFormat: RootJsonFormat[ShoppingCartPresenter] = jsonFormat2(ShoppingCartPresenter)
  case class ShoppingCartPresenter(id: String, items: Vector[ShopItem])
  class CheckoutController(generateId: () => String = () => "randomId", initialCarts: Map[String, ShoppingCart] = Map()) {
    var carts : Map[String, ShoppingCart] = initialCarts

    def buildCart = {
      val id = generateId()
      carts = carts + (id -> ShoppingCart(Vector()))
      ShoppingCartPresenter(id, Vector())
    }

    def addToCart : AddItemToCart => ShoppingCartPresenter = cmd => {
      val cart = carts(cmd.cartId)
      val newCart = ShoppingCart(cart.items :+ cmd.item)
      carts = carts + (cmd.cartId -> newCart)
      ShoppingCartPresenter(cmd.cartId, newCart.items)
    }

    val route = pathPrefix("checkout"){
      path("new"){
        get{
          complete(buildCart)
        }
      } ~
      path("add"){
        post{
          handleWith(addToCart)
        }
      }
    }
  }
}
