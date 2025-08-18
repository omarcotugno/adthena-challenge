package com.example.pricebasket.core.rules

import org.scalatest.funsuite.AnyFunSuite
import com.example.pricebasket.core.model._
import com.example.pricebasket.core.money.Money._

class PercentageOffPerItemSpec extends AnyFunSuite {
  private val catalogue = Map(
    "Apples" -> Product("Apples", gbp(1.00))
  )

  test("no discount when qty is zero") {
    val rule = PercentageOffPerItem("Apples", BigDecimal("0.10"), "Apples 10% off")
    assert(rule.compute(Map("Apples" -> 0L), catalogue).isEmpty)
  }

  test("10% per item on apples") {
    val rule = PercentageOffPerItem("Apples", BigDecimal("0.10"), "Apples 10% off")
    val res  = rule.compute(Map("Apples" -> 3L), catalogue)
    assert(res.contains(DiscountLine("Apples 10% off", gbp(0.30))))
  }

  test("unknown product returns None") {
    val rule = PercentageOffPerItem("Oranges", BigDecimal("0.10"), "Oranges 10% off")
    assert(rule.compute(Map("Oranges" -> 2L), catalogue).isEmpty)
  }
}
