package com.example.pricebasket.core.rules

import com.example.pricebasket.core.model.{DiscountLine, Product}
import com.example.pricebasket.core.money.Money

final case class LinkedMultiBuyDiscount(
    requiredProduct: String,
    requiredQty: Int,
    discountedProduct: String,
    discountPct: BigDecimal,
    label: String
) extends DiscountRule {
  def compute(counts: Map[String, Long], catalogue: Map[String, Product]): Option[DiscountLine] = {
    val reqCount  = counts.getOrElse(requiredProduct, 0L)
    val discCount = counts.getOrElse(discountedProduct, 0L)
    if (reqCount < requiredQty || discCount <= 0) None
    else {
      val eligible = math.min((reqCount / requiredQty).toInt, discCount.toInt)
      if (eligible <= 0) None
      else
        catalogue.get(discountedProduct).flatMap { prod =>
          val discount = Money.round(prod.price * BigDecimal(eligible) * discountPct)
          if (discount > 0) Some(DiscountLine(label, discount)) else None
        }
    }
  }
}
