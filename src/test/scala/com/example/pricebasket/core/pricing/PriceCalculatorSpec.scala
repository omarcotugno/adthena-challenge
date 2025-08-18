package com.example.pricebasket.core.pricing

import org.scalatest.funsuite.AnyFunSuite
import com.example.pricebasket.core.model._
import com.example.pricebasket.core.catalogue.Catalogue
import com.example.pricebasket.infra.config.ConfigCatalogue
import com.typesafe.config.ConfigFactory
import com.example.pricebasket.core.rules._
import com.example.pricebasket.core.money.Money._

class PriceCalculatorSpec extends AnyFunSuite {

  private val conf           = ConfigFactory.parseString("""
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
  private val cat: Catalogue = ConfigCatalogue.from(conf, "pricebasket")

  test("Empty basket error") {
    val calc = PriceCalculator(cat, Seq.empty)
    assert(calc.price(Map.empty).left.exists(_ == DomainError.EmptyBasket))
  }

  test("Subtotal is sum of price*qty") {
    val calc = PriceCalculator(cat, Seq.empty)
    val res  = calc.price(Map("Soup" -> 2L, "Bread" -> 1L)).toOption.get
    assert(res.subtotal == gbp(0.65 * 2 + 0.80))
    assert(res.discounts.isEmpty)
    assert(res.total == res.subtotal)
  }

  test("Discounts are applied by rules") {
    val rules = Seq(
      PercentageOffPerItem("Apples", BigDecimal("0.10"), "Apples 10% off"),
      LinkedMultiBuyDiscount("Soup", 2, "Bread", BigDecimal("0.50"), "Bread 50% off (with 2 Soup)")
    )
    val calc  = PriceCalculator(cat, rules)
    val res   = calc.price(Map("Apples" -> 2L, "Soup" -> 2L, "Bread" -> 1L)).toOption.get
    // subtotal
    assert(res.subtotal == gbp(2 * 1.00 + 2 * 0.65 + 0.80))
    // discounts: apples 20% of £1 each = £0.20; bread 50% of £0.80 = £0.40
    assert(res.discounts.map(_.amount).sum == gbp(0.60))
    assert(res.total == gbp((2 * 1.00 + 2 * 0.65 + 0.80) - 0.60))
  }
}
