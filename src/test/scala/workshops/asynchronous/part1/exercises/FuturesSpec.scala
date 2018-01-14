package workshops.asynchronous.part1.exercises

import workshops.UnitSpec

import scala.concurrent.Future

class FuturesSpec extends UnitSpec{

}

object FuturesSpec{

  case class Item(name: String, price: Int)
  case class Discount(value: Int)
  case class Order(id: String, items: Seq[Item], discounts: Seq[Discount])

  class OrderRepository(initialOrders: Seq[Order]){
    def getOrInitializeOrderById(id: String): Future[Order] = ???

  }

  object OrderService{
    def orderTodalPrice(order: Order): Int = itemsTotalPrice(order.items) - discountTotal(order.discounts)
    def itemsTotalPrice(items: Seq[Item]): Int = items.map(_.price).sum
    def discountTotal(discounts: Seq[Discount]): Int = discounts.map(_.value).sum
  }

  class LegacyStockRepostiory(initialStock: Map[String, Int]){
    private var currentStock = initialStock

    def reserveItem(itemName: String): Unit = {
      currentStock = currentStock ++ Map(itemName -> (initialStock(itemName) -1))
    }

    def removeReservation(itemName: String): Unit = {
      currentStock = currentStock ++ Map(itemName -> (initialStock(itemName) +1))
    }
  }

  object DiscountCalculator{
    def calculateDiscount(items: Seq[Item]): Discount = {
      if(OrderService.itemsTotalPrice(items) > 50) Discount(5)
      else Discount(0)
    }
  }
}
