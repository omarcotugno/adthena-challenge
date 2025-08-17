package com.example.pricebasket.core.rules

import com.example.pricebasket.core.model.{DiscountLine, Product}

/** Optional syntactic helpers for composing/limiting rules. */
object RuleCombinators {
  implicit final class RuleOps(private val rule: DiscountRule) extends AnyVal {

    /** Cap the max times a rule can apply by clamping the computed discount amount to a max
      * multiplier. This is a simplistic cap: it scales down the computed amount if needed.
      */
    def capped(maxAmount: BigDecimal): DiscountRule =
      new DiscountRule {
        val label: String = s"${rule.label} (capped)"
        def compute(
            counts: Map[String, Long],
            catalogue: Map[String, Product]
        ): Option[DiscountLine] =
          rule.compute(counts, catalogue).map { dl =>
            if (dl.amount > maxAmount) dl.copy(amount = maxAmount) else dl
          }
      }
  }
}
