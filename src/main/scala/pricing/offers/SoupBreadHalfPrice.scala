package pricing.offers

import pricing.{Discount, Offer, OfferContext}

/** Buy 2 tins of soup and get a loaf of bread for half price. For every 2 soups, up to one bread
  * gets 50% off.
  */
class SoupBreadHalfPrice extends Offer {
  private val Soup  = "soup"
  private val Bread = "bread"
  private val Label = "Buy 2 Soup, Bread half price"

  def evaluate(ctx: OfferContext) = {
    val soupQty  = ctx.counts.getOrElse(Soup, 0)
    val breadQty = ctx.counts.getOrElse(Bread, 0)
    if (soupQty < 2 || breadQty == 0) None
    else {
      val eligibleBreads = math.min(soupQty / 2, breadQty)
      val breadUnit      = ctx.unitPrices(Bread)
      val discount       = (breadUnit * eligibleBreads) / 2
      if (discount > 0) Some(Discount(Label, discount)) else None
    }
  }
}
