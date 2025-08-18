package com.example.pricebasket.core.rules

import com.example.pricebasket.core.model.{DiscountLine, Product}
import com.example.pricebasket.core.money.Money
import java.time.LocalDate

final case class PercentageOffPerItem(
    productName: String,
    pct: BigDecimal,
    label: String,
    override val startDate: Option[LocalDate] = None,
    override val endDate: Option[LocalDate] = None
) extends DiscountRule {

  def compute(counts: Map[String, Long], catalogue: Map[String, Product]): Option[DiscountLine] = {
    if (!isActive()) return None

    val qty = counts.getOrElse(productName, 0L)
    if (qty <= 0) None
    else
      catalogue.get(productName).flatMap { prod =>
        val discount = Money.round(prod.price * BigDecimal(qty) * pct)
        if (discount > 0) Some(DiscountLine(label, discount)) else None
      }
  }
}
