package com.example.pricebasket.core.rules

import org.scalatest.funsuite.AnyFunSuite
import com.example.pricebasket.core.model._
import com.example.pricebasket.core.money.Money._

class LinkedMultiBuyDiscountSpec extends AnyFunSuite {
  private val catalogue = Map(
    "Soup"  -> Product("Soup", gbp(0.65)),
    "Bread" -> Product("Bread", gbp(0.80))
  )

  test("requires threshold of required product and at least one discounted item") {
    val rule = LinkedMultiBuyDiscount("Soup", 2, "Bread", BigDecimal("0.50"), "Bread 50% off")
    assert(rule.compute(Map("Soup" -> 1L, "Bread" -> 1L), catalogue).isEmpty)
    assert(rule.compute(Map("Soup" -> 2L, "Bread" -> 0L), catalogue).isEmpty)
  }

  test("applies to min(eligible sets, discounted qty)") {
    val rule = LinkedMultiBuyDiscount("Soup", 2, "Bread", BigDecimal("0.50"), "Bread 50% off")

    // 2 soups + 1 bread => 1 half-price bread = 0.40
    assert(rule.compute(Map("Soup" -> 2L, "Bread" -> 1L), catalogue)
      .contains(DiscountLine("Bread 50% off", gbp(0.40))))

    // 4 soups + 3 breads => min(2,3)=2 half-price breads = 0.80
    assert(rule.compute(Map("Soup" -> 4L, "Bread" -> 3L), catalogue)
      .contains(DiscountLine("Bread 50% off", gbp(0.80))))
  }

  test("unknown discounted product returns None") {
    val rule = LinkedMultiBuyDiscount("Soup", 2, "Baguette", BigDecimal("0.50"), "Baguette 50% off")
    assert(rule.compute(Map("Soup" -> 2L, "Baguette" -> 1L), catalogue).isEmpty)
  }
}
