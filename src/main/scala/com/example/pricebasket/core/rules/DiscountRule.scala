package com.example.pricebasket.core.rules

import com.example.pricebasket.core.model.{DiscountLine, Product}

trait DiscountRule {
  def label: String

  /** @param counts
    *   item -> quantity (already normalised)
    * @param catalogue
    *   name -> Product
    */
  def compute(counts: Map[String, Long], catalogue: Map[String, Product]): Option[DiscountLine]
}
