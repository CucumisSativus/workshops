package workshops.akkaHttp.part3.exercises

import java.util.NoSuchElementException

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import spray.json.{RootJsonFormat, _}
import workshops.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JsonHandlingSpec3 extends UnitSpec with ScalatestRouteTest {

  import JsonHandlingSpec3._

  "Checkout controller" should {
    "return a cart when present in the database" in {
      val cartId = "myId"
      val cart = ShoppingCart(Vector(ShopItem("item", BigDecimal(123))))
      val controller = new CheckoutController(initialCarts = Map(cartId -> cart))
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

      Get("/checkout/myId") ~> controller.route ~> check {
        responseAs[String].parseJson mustBe expectedJson
      }
    }

    "return 404 when item is not present in the database" in {
      // use completeOrRecoverWith
      val controller = new CheckoutController()
      Get("/checkout/myId") ~> controller.route ~> check {
        status mustBe StatusCodes.NotFound
      }
    }

    "initialize new shopping cart" in {
      // complete will be enough here
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
      // use handleWith
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

      val requestJson = """{"cartId":"myId","item":{"name":"item","price":123}}"""

      Post("/checkout/add").withEntity(ContentTypes.`application/json`, requestJson) ~> controller.route ~> check {
        responseAs[String].parseJson mustBe expectedJson
      }

      controller.carts(cartId) mustBe ShoppingCart(Vector(newItem))
    }
  }

}

object JsonHandlingSpec3 {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import spray.json.DefaultJsonProtocol._

  case class ShopItem(name: String, price: BigDecimal)

  case class AddItemToCart(cartId: String, item: ShopItem)

  case class ShoppingCart(items: Vector[ShopItem] = Vector())

  case class ShoppingCartPresenter(id: String, items: Vector[ShopItem])

  object ShoppingCartPresenter {
    def empty(id: String) = ShoppingCartPresenter(id, Vector())

    def fromCart(id: String, cart: ShoppingCart) = ShoppingCartPresenter(id, cart.items)
  }

  // uncomment and fill
  //  implicit val itemFormat: RootJsonFormat[ShopItem]
  //  implicit val addToCartFormat: RootJsonFormat[AddItemToCart]
  //  implicit val cartFormat: RootJsonFormat[ShoppingCartPresenter]
  class CheckoutController(generateId: () => String = () => "randomId", initialCarts: Map[String, ShoppingCart] = Map()) {
    var carts: Map[String, ShoppingCart] = initialCarts

    private def buildCart = {
      val id = generateId()
      carts = carts + (id -> ShoppingCart(Vector()))
      ShoppingCartPresenter.empty(id)
    }

    private def addToCart: AddItemToCart => ShoppingCartPresenter = cmd => {
      val cart = carts(cmd.cartId)
      val newCart = ShoppingCart(cart.items :+ cmd.item)
      carts = carts + (cmd.cartId -> newCart)
      ShoppingCartPresenter.fromCart(cmd.cartId, newCart)
    }

    val getCartAndPresent = (cartId: String) => Future(carts(cartId)).map(c => ShoppingCartPresenter.fromCart(cartId, c))

    val route = pathPrefix("checkout") {
      path("new") {
        get {
          ???
        }
      } ~
        path("add") {
          post {
            ???
          }
        } ~ path(Segment) { cartId =>
        ???
      }
    }
  }

}
