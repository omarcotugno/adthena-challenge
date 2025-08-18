package com.example.pricebasket.core.money

import org.scalatest.funsuite.AnyFunSuite

class MoneySpec extends AnyFunSuite {
  test("rounding to 2 decimals HALF_UP") {
    assert(Money.round(BigDecimal("1.234")) == BigDecimal("1.23"))
    assert(Money.round(BigDecimal("1.235")) == BigDecimal("1.24"))
  }
  test("gbp constructors") {
    assert(Money.gbp(1.234) == BigDecimal("1.23"))
    assert(Money.gbp(BigDecimal("2.345")) == BigDecimal("2.35"))
  }
}
