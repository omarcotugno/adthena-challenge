package com.example.pricebasket.infra.spark

import org.scalatest.funsuite.AnyFunSuite
import org.apache.spark.sql.SparkSession
import com.example.pricebasket.infra.config.ConfigCatalogue
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
    val offers = Offers.default
    val calc   = PriceCalculator(cat, offers.rules)
    val eng    = new SparkPricingEngine(calc, cat)

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
