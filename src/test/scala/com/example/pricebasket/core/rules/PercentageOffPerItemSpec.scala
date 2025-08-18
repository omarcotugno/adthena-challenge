package com.example.pricebasket.core.rules

import org.scalatest.funsuite.AnyFunSuite
import com.example.pricebasket.core.model._
import com.example.pricebasket.core.money.Money._
import java.time.LocalDate

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

  test("discount applies only within validity window (inclusive)") {
    val today = LocalDate.now()
    val rule  = PercentageOffPerItem(
      "Apples",
      BigDecimal("0.10"),
      "Apples 10% off",
      startDate = Some(today.minusDays(1)),
      endDate = Some(today.plusDays(1))
    )

    val res = rule.compute(Map("Apples" -> 2L), catalogue)
    assert(res.contains(DiscountLine("Apples 10% off", gbp(0.20))))
  }

  test("no discount before start date") {
    val today = LocalDate.now()
    val rule  = PercentageOffPerItem(
      "Apples",
      BigDecimal("0.10"),
      "Apples 10% off (future)",
      startDate = Some(today.plusDays(1)),
      endDate = Some(today.plusDays(10))
    )

    val res = rule.compute(Map("Apples" -> 2L), catalogue)
    assert(res.isEmpty)
  }

  test("no discount after end date") {
    val today = LocalDate.now()
    val rule  = PercentageOffPerItem(
      "Apples",
      BigDecimal("0.10"),
      "Apples 10% off (expired)",
      startDate = Some(today.minusDays(10)),
      endDate = Some(today.minusDays(1))
    )

    val res = rule.compute(Map("Apples" -> 2L), catalogue)
    assert(res.isEmpty)
  }

  test("open-ended start date works (only start provided)") {
    val today = LocalDate.now()
    val rule  = PercentageOffPerItem(
      "Apples",
      BigDecimal("0.10"),
      "Apples 10% off (started)",
      startDate = Some(today.minusDays(5)),
      endDate = None
    )

    val res = rule.compute(Map("Apples" -> 1L), catalogue)
    assert(res.contains(DiscountLine("Apples 10% off (started)", gbp(0.10))))
  }

  test("open-ended end date works (only end provided)") {
    val today = LocalDate.now()
    val rule  = PercentageOffPerItem(
      "Apples",
      BigDecimal("0.10"),
      "Apples 10% off (until today)",
      startDate = None,
      endDate = Some(today)
    )

    val res = rule.compute(Map("Apples" -> 1L), catalogue)
    assert(res.contains(DiscountLine("Apples 10% off (until today)", gbp(0.10))))
  }
}
