package com.example.pricebasket.core.rules

import com.example.pricebasket.core.model.{DiscountLine, Product}
import java.time.LocalDate

trait DiscountRule {
  def label: String

  /** Optional start date (inclusive) of the offer's validity window. */
  def startDate: Option[LocalDate] = None

  /** Optional end date (inclusive) of the offer's validity window. */
  def endDate: Option[LocalDate] = None

  /** Returns true if the rule is active for the given date. If no bounds are provided, the rule is
    * considered always active.
    *
    * @param today
    *   the date to evaluate activity against (defaults to current date)
    */
  protected def isActive(today: LocalDate = LocalDate.now()): Boolean = {
    val startsOk = startDate.forall(d => !today.isBefore(d)) // today >= start
    val endsOk   = endDate.forall(d => !today.isAfter(d))    // today <= end
    startsOk && endsOk
  }

  /** @param counts
    *   item -> quantity (already normalised)
    * @param catalogue
    *   name -> Product
    */
  def compute(counts: Map[String, Long], catalogue: Map[String, Product]): Option[DiscountLine]
}
