package cli

import domain.{Basket, Product}
import pricing.PricingService
import util.Money

object PriceBasketApp {
  def run(args: Array[String]): Int = {
    if (args.isEmpty) {
      Console.err.println("Usage: PriceBasket item1 item2 ...")
      return 2
    }

    val parsed = args.toList.map(Product.fromString)
    val errors = parsed.collect { case Left(err) => err }
    if (errors.nonEmpty) {
      errors.foreach(e => Console.err.println(e))
      return 2
    }

    val items   = parsed.collect { case Right(p) => p }
    val basket  = Basket(items)
    val service = PricingService.default
    val result  = service.price(basket)

    println(s"Subtotal: ${Money.format(result.subtotal)}")
    if (result.discounts.isEmpty) {
      println("(No offers available)")
    } else {
      result.discounts.foreach { d =>
        println(s"${d.label}: ${Money.format(d.amount)}")
      }
    }
    println(s"Total price: ${Money.format(result.total)}")
    0
  }
}
