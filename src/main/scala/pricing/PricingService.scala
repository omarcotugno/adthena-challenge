package pricing

import domain.Basket
import domain.Product
import util.Money.Pence

final case class Result(subtotal: Pence, discounts: List[Discount], total: Pence)

class PricingService(offers: List[Offer]) {

  def price(basket: Basket): Result = {

    val subtotal = basket.items.map(_.unitPrice).sum

    val ctx = OfferContext(
      counts = Product.all.map(p => p.code -> basket.count(p)).toMap,
      unitPrices = Product.all.map(p => p.code -> p.unitPrice).toMap
    )

    val discounts = offers.flatMap(_.evaluate(ctx))
    val total     = subtotal - discounts.map(_.amount).sum
    Result(subtotal, discounts, total)
  }
}

object PricingService {
  def default: PricingService =
    new PricingService(
      offers = List(
        new offers.ApplesTenPercent,
        new offers.SoupBreadHalfPrice
      )
    )
}
