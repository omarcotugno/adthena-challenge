package com.example.pricebasket.infra.spark

import java.time.LocalDate
import org.scalatest.funsuite.AnyFunSuite
import org.apache.spark.sql.SparkSession
import com.example.pricebasket.infra.config.{ConfigCatalogue, OffersLoader}
import com.typesafe.config.ConfigFactory
import com.example.pricebasket.core.pricing.{Offers, PriceCalculator}
import com.example.pricebasket.core.model._

class SparkPricingEngineSpec extends AnyFunSuite {

  private val conf = ConfigFactory.parseString("""
      |pricebasket {
      |  products = [
      |    { name = "Soup",   price = 0.65 },
      |    { name = "Bread",  price = 0.80 },
      |    { name = "Milk",   price = 1.30 },
      |    { name = "Apples", price = 1.00 }
      |  ]
      |  normaliser = [
      |    { from = ["soup","soups"], to = "Soup" },
      |    { from = ["bread"],        to = "Bread" },
      |    { from = ["milk"],         to = "Milk" },
      |    { from = ["apple","apples"], to = "Apples" }
      |  ]
      |}
      |""".stripMargin)
  private val cat  = ConfigCatalogue.from(conf, "pricebasket")

  private def withSpark[A](f: SparkSession => A): A = {
    val spark = SparkSession.builder().appName("test").master("local[*]").getOrCreate()
    try f(spark)
    finally spark.stop()
  }

  test("end-to-end pricing with no unknown items") {
    // cat defined above
    val today      = LocalDate.now()
    val offersConf = ConfigFactory.parseString(s"""
        |pricebasket {
        |  offers = [
        |    { type = "percentage-per-item", product = "Apples", pct = 0.10, label = "Apples 10% off",
        |      startDate = "${today.minusDays(1)}", endDate = "${today.plusDays(5)}" },
        |    { type = "linked-multibuy", requiredProduct = "Soup", requiredQty = 2,
        |      discountedProduct = "Bread", discountPct = 0.50, label = "Bread 50% off (with 2 Soup)" }
        |  ]
        |}
        |""".stripMargin)
    val rules      = OffersLoader.load(offersConf, "pricebasket")
    val calc       = PriceCalculator(cat, rules)
    val eng        = new SparkPricingEngine(calc, cat)

    val res = withSpark { implicit spark =>
      eng.price(Seq("Apples", "Soup", "Soup", "Bread"))
    }

    assert(res.isRight)
    val r = res.toOption.get
    assert(r.subtotal > 0)
    assert(r.total <= r.subtotal) // discounts never increase price
  }

  test("Unknown items are reported") {
    // cat defined above
    val calc = PriceCalculator(cat, Seq.empty)
    val eng  = new SparkPricingEngine(calc, cat)

    val res = withSpark { implicit spark =>
      eng.price(Seq("UnknownItem"))
    }
    assert(res.left.exists(_.isInstanceOf[DomainError.UnknownItems]))
  }
}
