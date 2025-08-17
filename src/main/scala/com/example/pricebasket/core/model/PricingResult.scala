package com.example.pricebasket.core.model

import com.example.pricebasket.core.money.Money._

final case class PricingResult(subtotal: BigDecimal, discounts: Seq[DiscountLine]) {
  lazy val total: BigDecimal = round(
    subtotal - discounts.map(_.amount).foldLeft(BigDecimal(0))(_ + _)
  )
}
