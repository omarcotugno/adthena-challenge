package com.example.pricebasket.core.pricing

import com.example.pricebasket.core.model._
import com.example.pricebasket.core.catalogue.Catalogue
import com.example.pricebasket.core.money.Money._
import com.example.pricebasket.core.rules.DiscountRule

final case class PriceCalculator(catalogue: Catalogue, rules: Seq[DiscountRule]) {

  /** Pure pricing: counts must be normalised to catalogue keys. */
  def price(counts: Map[String, Long]): Either[DomainError, PricingResult] = {
    if (counts.isEmpty) return Left(DomainError.EmptyBasket)
    val subtotal  = round(counts.foldLeft(BigDecimal(0)) { case (acc, (name, qty)) =>
      catalogue.products.get(name).map(_.price * BigDecimal(qty)).getOrElse(BigDecimal(0)) + acc
    })
    val discounts = rules.flatMap(_.compute(counts, catalogue.products))
    Right(PricingResult(subtotal, discounts))
  }
}
