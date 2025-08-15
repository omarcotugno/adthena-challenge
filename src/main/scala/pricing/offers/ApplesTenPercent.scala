package pricing.offers

import pricing.{Discount, Offer, OfferContext}
import util.Money

/** Apples have 10% off their normal price this week */
class ApplesTenPercent extends Offer {
  private val Code  = "apples"
  private val Label = "Apples 10% off"

  def evaluate(ctx: OfferContext) = {
    val qty = ctx.counts.getOrElse(Code, 0)
    if (qty <= 0) None
    else {
      val unit           = ctx.unitPrices(Code)
      val subtotalApples = unit * qty
      val discount       = Money.percent(subtotalApples, BigDecimal(10))
      if (discount > 0) Some(Discount(Label, discount)) else None
    }
  }
}
