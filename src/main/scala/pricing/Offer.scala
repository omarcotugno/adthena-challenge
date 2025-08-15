package pricing

import util.Money.Pence

final case class Discount(label: String, amount: Pence) {
  require(amount >= 0, "discount must be non-negative")
}

trait Offer {
  def evaluate(ctx: OfferContext): Option[Discount]
}

final case class OfferContext(
    counts: Map[String, Int],      // product code -> qty
    unitPrices: Map[String, Pence] // product code -> price in pence
)
