package com.example.pricebasket.core.money

import scala.math.BigDecimal.RoundingMode

object Money {
  val Scale                            = 2
  val Mode: RoundingMode.Value         = RoundingMode.HALF_UP
  def round(v: BigDecimal): BigDecimal = v.setScale(Scale, Mode)
  def gbp(v: Double): BigDecimal       = round(BigDecimal(v))
  def gbp(v: BigDecimal): BigDecimal   = round(v)
}
