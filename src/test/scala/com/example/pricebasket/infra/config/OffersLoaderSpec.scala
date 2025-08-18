package com.example.pricebasket.infra.config

import org.scalatest.funsuite.AnyFunSuite
import com.typesafe.config.ConfigFactory
import com.example.pricebasket.core.rules._
import com.typesafe.config.ConfigException
import java.time.LocalDate

class OffersLoaderSpec extends AnyFunSuite {
  test("parses percentage-per-item and linked-multibuy") {
    val conf  = ConfigFactory.parseString("""
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

  test("parses optional startDate/endDate for percentage-per-item and linked-multibuy") {
    val today = LocalDate.now()
    val conf  = ConfigFactory.parseString(s"""
         |pricebasket {
         |  offers = [
         |    { type = "percentage-per-item", product = "Apples", pct = 0.10, label = "Apples 10% off",
         |      startDate = "${today.minusDays(1)}", endDate = "${today.plusDays(1)}" },
         |    { type = "linked-multibuy", requiredProduct = "Soup", requiredQty = 2,
         |      discountedProduct = "Bread", discountPct = 0.50, label = "Bread 50% off (with 2 Soup)",
         |      startDate = "${today}", endDate = "${today.plusDays(30)}" }
         |  ]
         |}
         |""".stripMargin)

    val rules = OffersLoader.load(conf, "pricebasket")
    val p     = rules.collectFirst { case r: PercentageOffPerItem => r }.get
    assert(p.startDate.contains(today.minusDays(1)))
    assert(p.endDate.contains(today.plusDays(1)))

    val l = rules.collectFirst { case r: LinkedMultiBuyDiscount => r }.get
    assert(l.startDate.contains(today))
    assert(l.endDate.contains(today.plusDays(30)))
  }

  test("omitting dates keeps rules always active (None/None)") {
    val conf = ConfigFactory.parseString("""
        |pricebasket {
        |  offers = [
        |    { type = "percentage-per-item", product = "Apples", pct = 0.10, label = "Apples 10% off" },
        |    { type = "linked-multibuy", requiredProduct = "Soup", requiredQty = 2,
        |      discountedProduct = "Bread", discountPct = 0.50, label = "Bread 50% off (with 2 Soup)" }
        |  ]
        |}
        |""".stripMargin)

    val rules = OffersLoader.load(conf, "pricebasket")
    val p     = rules.collectFirst { case r: PercentageOffPerItem => r }.get
    assert(p.startDate.isEmpty && p.endDate.isEmpty)

    val l = rules.collectFirst { case r: LinkedMultiBuyDiscount => r }.get
    assert(l.startDate.isEmpty && l.endDate.isEmpty)
  }

  test("invalid date format throws ConfigException.BadValue") {
    val conf = ConfigFactory.parseString("""
        |pricebasket {
        |  offers = [
        |    { type = "percentage-per-item", product = "Apples", pct = 0.10, label = "Apples 10% off",
        |      startDate = "08-01-2025", endDate = "2025-08-31" }
        |  ]
        |}
        |""".stripMargin)

    intercept[ConfigException.BadValue] {
      OffersLoader.load(conf, "pricebasket")
    }
  }

  test("startDate after endDate throws ConfigException.BadValue") {
    val conf = ConfigFactory.parseString("""
        |pricebasket {
        |  offers = [
        |    { type = "linked-multibuy", requiredProduct = "Soup", requiredQty = 2,
        |      discountedProduct = "Bread", discountPct = 0.50, label = "Bread 50% off (with 2 Soup)",
        |      startDate = "2025-09-01", endDate = "2025-08-01" }
        |  ]
        |}
        |""".stripMargin)

    intercept[ConfigException.BadValue] {
      OffersLoader.load(conf, "pricebasket")
    }
  }
}
