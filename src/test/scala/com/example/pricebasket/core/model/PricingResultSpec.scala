package com.example.pricebasket.core.model

import org.scalatest.funsuite.AnyFunSuite
import com.example.pricebasket.core.money.Money._

class PricingResultSpec extends AnyFunSuite {

  test("total equals subtotal when there are no discounts") {
    val pr = PricingResult(subtotal = gbp(5.00), discounts = Seq.empty)
    assert(pr.total == gbp(5.00))
  }

  test("total subtracts the sum of discounts (rounded)") {
    val discounts = Seq(
      DiscountLine("Offer A", gbp(0.40)),
      DiscountLine("Offer B", gbp(1.15))
    )
    val pr = PricingResult(subtotal = gbp(10.00), discounts = discounts)
    // 10.00 - (0.40 + 1.15) = 8.45
    assert(pr.total == gbp(8.45))
  }

  test("handles many small discounts without penny drift") {
    val tiny = List.fill(10)(DiscountLine("tiny", gbp(0.01)))
    val pr = PricingResult(subtotal = gbp(1.00), discounts = tiny)
    // 1.00 - 0.10 = 0.90
    assert(pr.total == gbp(0.90))
  }
}
