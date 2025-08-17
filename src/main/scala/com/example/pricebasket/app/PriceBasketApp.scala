package com.example.pricebasket.app

import org.apache.spark.sql.SparkSession
import com.example.pricebasket.infra.spark.SparkPricingEngine
import com.example.pricebasket.infra.config.AppConfig
import com.example.pricebasket.core.model._

object PriceBasketApp {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      Console.err.println("Usage: PriceBasket <item1> <item2> ...")
      sys.exit(1)
    }

    implicit val spark: SparkSession = SparkSession
      .builder()
      .appName("price-basket")
      .master(sys.props.getOrElse("spark.master", "local[*]"))
      .getOrCreate()

    val catalogue  = AppConfig.catalogue
    val calculator = AppConfig.calculator
    val engine     = new SparkPricingEngine(calculator, catalogue)

    try
      engine.price(args.toSeq) match {
        case Left(DomainError.UnknownItems(items, known)) =>
          Console.err.println(
            s"Unknown item(s): ${items.mkString(", ")}. Known items: ${known.sorted.mkString(", ")}"
          )
          sys.exit(2)
        case Left(DomainError.EmptyBasket)                =>
          Console.err.println("Basket is empty.")
          sys.exit(2)
        case Right(result)                                =>
          println(s"Subtotal: ${Formatters.formatMoney(result.subtotal)}")
          if (result.discounts.isEmpty) println("(No offers available)")
          else
            result.discounts
              .foreach(d => println(s"${d.label}: ${Formatters.formatDiscount(d.amount)}"))
          println(s"Total price: ${Formatters.formatMoney(result.total)}")
      }
    finally
      spark.stop()
  }

  object Formatters {
    def formatMoney(amount: BigDecimal): String    = f"£$amount%.2f"
    def formatDiscount(amount: BigDecimal): String =
      if (amount < BigDecimal(1)) s"${(amount * 100).setScale(0)}p" else formatMoney(amount)
  }
}
