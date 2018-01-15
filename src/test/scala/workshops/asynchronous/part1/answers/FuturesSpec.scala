package workshops.asynchronous.part1.answers

import org.scalatest.concurrent.ScalaFutures
import workshops.UnitSpec
import workshops.asynchronous.part1.answers.FuturesSpec.CheckoutController.{ExecutionError, ItemAdded, ItemCannotBeAdded}
import workshops.asynchronous.part1.answers.FuturesSpec.StockRepositoryACL.{NotEnoughItems, ReservationSuccessful, ReserveItemResult}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure
class FuturesSpec extends UnitSpec with ScalaFutures{
  import workshops.asynchronous.part1.answers.FuturesSpec._
  import scala.concurrent.ExecutionContext.Implicits.global
  "Full spec" should {
    "initialize order if not existing" in {
      val repo = new OrderRepository()

      whenReady(repo.getOrInitializeOrderById("id")){ order =>
        order mustBe Order(id = "id", items = Seq.empty)
      }
    }
    "return existing order if saved to the database" in {
      val expectedOrder = Order("id", items = Seq(Item("item")))
      val repo = new OrderRepository()
      repo.orders = Set(expectedOrder)

      whenReady(repo.getOrInitializeOrderById("id")){ order =>
        order mustBe expectedOrder
      }
    }

    "reserve item that is in the database with stock repository" in {
      val initialStock = Map( "item" -> 1)
      val legacyStockRepostiory = new LegacyStockRepostiory(initialStock)
      val stockRepositoryACL = new StockRepositoryACL(legacyStockRepostiory)

      whenReady(stockRepositoryACL.reserveItem("item")){ result =>
        result mustBe StockRepositoryACL.ReservationSuccessful

        legacyStockRepostiory.currentStock mustBe Map("item" -> 0)
      }
    }

    "fail with reservation if item has not enough stock" in {
      val initialStock = Map[String, Int]()
      val legacyStockRepostiory = new LegacyStockRepostiory(initialStock)
      val stockRepositoryACL = new StockRepositoryACL(legacyStockRepostiory)

      whenReady(stockRepositoryACL.reserveItem("item")){ result =>
        result mustBe StockRepositoryACL.NotEnoughItems
      }
    }

    "add item to cart if everything goes well" in {
      val initialStock = Map("item" -> 1)
      val legacyStockRepostiory = new LegacyStockRepostiory(initialStock)
      val stockRepositoryACL = new StockRepositoryACL(legacyStockRepostiory)
      val repo = new OrderRepository()
      val controller = new CheckoutController(repo, stockRepositoryACL)

      whenReady(controller.addItemToCart("id", Item("item"))){ result =>
        result mustBe CheckoutController.ItemAdded
        repo.orders mustBe Set(Order("id", Seq(Item("item"))))

        legacyStockRepostiory.currentStock mustBe Map("item" -> 0)
      }
    }

    "fail when stock is not enough" in {
      val initialStock = Map("item" -> 1)
      val legacyStockRepostiory = new LegacyStockRepostiory(initialStock)
      val stockRepositoryACL = new StockRepositoryACL(legacyStockRepostiory)
      val repo = new OrderRepository()
      val controller = new CheckoutController(repo, stockRepositoryACL)

      whenReady(controller.addItemToCart("id", Item("anotherItem"))){ result =>
        result mustBe CheckoutController.ItemCannotBeAdded

        repo.orders mustBe Set.empty
      }
    }

    // Level boss
    "release stock in case of problems with saving order" in {
      val initialStock = Map("item" -> 1)
      val legacyStockRepostiory = new LegacyStockRepostiory(initialStock)
      val stockRepositoryACL = new StockRepositoryACL(legacyStockRepostiory)
      val repo = OrderRepositoryWhichFailsOnSave
      val controller = new CheckoutController(repo, stockRepositoryACL)

      whenReady(controller.addItemToCart("id", Item("item"))){ result =>
        result mustBe CheckoutController.ExecutionError

        legacyStockRepostiory.currentStock mustBe initialStock
      }
    }
  }
}

private[answers] object FuturesSpec{

  case class Item(name: String)
  case class Order(id: String, items: Seq[Item] = Seq.empty){
    def appendItem(item: Item): Order = copy(items = items :+ item)
  }

  class OrderRepository() {
    var orders: Set[Order] = Set.empty
    // do not save order here just yet
    def getOrInitializeOrderById(id: String)(implicit ec: ExecutionContext): Future[Order] =
      Future.successful(orders.find(_.id ==id).getOrElse(Order(id)))

    def saveOrder(order: Order)(implicit ec: ExecutionContext): Future[Unit] = {
      orders = orders + order
      Future.successful(Unit)
    }

  }


  class CheckoutController(orderRepository: OrderRepository, stockRepositoryACL: StockRepositoryACL){
    def addItemToCart(orderId: String, item: Item)(implicit ec: ExecutionContext): Future[CheckoutController.AddItemResult] = {

      orderRepository.getOrInitializeOrderById(orderId).flatMap { order =>
        stockRepositoryACL.reserveItem(item.name).flatMap {
          case NotEnoughItems => Future.successful(ItemCannotBeAdded)
          case ReservationSuccessful=>  performSafeSave(order, item: Item)
        }
      }
    }

    private def performSafeSave(order: Order, item: Item)(implicit ec: ExecutionContext): Future[CheckoutController.AddItemResult] = {
      orderRepository.saveOrder(order.appendItem(item)).map(_ => ItemAdded).andThen{
        case Failure(ex) => stockRepositoryACL.removeReservation(item.name)
        case _ =>
      }.recover{
        case ex: Throwable => ExecutionError
      }
    }
  }

  object CheckoutController{
    sealed trait AddItemResult
    case object ItemAdded extends AddItemResult
    case object ItemCannotBeAdded extends AddItemResult
    case object ExecutionError extends AddItemResult
  }


  class StockRepositoryACL(stockRepostiory: LegacyStockRepostiory){
    def reserveItem(itemName: String)(implicit ec: ExecutionContext): Future[ReserveItemResult] =
      Future(stockRepostiory.reserveItem(itemName)).map(_ => ReservationSuccessful).recover{ case _: Throwable => NotEnoughItems}
    def removeReservation(itemName: String)(implicit ec: ExecutionContext): Future[Unit] =
      Future(stockRepostiory.removeReservation(itemName))
  }

  object StockRepositoryACL{
    sealed trait ReserveItemResult
    case object ReservationSuccessful extends ReserveItemResult
    case object NotEnoughItems extends ReserveItemResult
  }

  class NotEnoughItemsException extends Exception("not enouth items!")
  class LegacyStockRepostiory(initialStock: Map[String, Int]){
    var currentStock = initialStock

    def reserveItem(itemName: String): Unit = {
      val currentItemsNumber = currentStock.lift(itemName).getOrElse(throw new NotEnoughItemsException)
      currentStock = currentStock ++ Map(itemName -> (currentItemsNumber-1))
    }

    def removeReservation(itemName: String): Unit = {
      currentStock = currentStock ++ Map(itemName -> (currentStock(itemName) +1))
    }
  }

  case object OrderRepositoryWhichFailsOnSave extends OrderRepository{
    override def saveOrder(order: Order)(implicit ec: ExecutionContext): Future[Unit] = Future.failed(new Exception("Database exception"))
  }
}
