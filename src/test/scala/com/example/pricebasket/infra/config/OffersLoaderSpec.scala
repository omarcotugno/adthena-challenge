package com.example.pricebasket.infra.config

import org.scalatest.funsuite.AnyFunSuite
import com.typesafe.config.ConfigFactory
import com.example.pricebasket.core.rules._

class OffersLoaderSpec extends AnyFunSuite {
  test("parses percentage-per-item and linked-multibuy") {
    val conf = ConfigFactory.parseString(
      """
        |pricebasket {
        |  offers = [
        |    { type = "percentage-per-item", product = "Apples", pct = 0.10, label = "Apples 10% off" },
        |    { type = "linked-multibuy", requiredProduct = "Soup", requiredQty = 2,
        |      discountedProduct = "Bread", discountPct = 0.50, label = "Bread 50% off (with 2 Soup)" }
        |  ]
        |}
        |""".stripMargin)
    val rules = OffersLoader.load(conf, "pricebasket")
    assert(rules.exists(_.isInstanceOf[PercentageOffPerItem]))
    assert(rules.exists(_.isInstanceOf[LinkedMultiBuyDiscount]))
    assert(rules.size == 2)
  }
}
