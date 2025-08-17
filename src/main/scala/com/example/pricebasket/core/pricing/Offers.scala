package com.example.pricebasket.core.pricing

import com.example.pricebasket.core.rules._

final case class Offers(rules: Seq[DiscountRule])

object Offers {

  /** Default behavior: Apples 10% off; Bread 50% off for each 2 Soups. */
  def default: Offers =
    Offers(
      Seq(
        PercentageOffPerItem("Apples", BigDecimal("0.10"), "Apples 10% off"),
        LinkedMultiBuyDiscount(
          "Soup",
          2,
          "Bread",
          BigDecimal("0.50"),
          "Bread 50% off (with 2 Soup)"
        )
      )
    )
}
