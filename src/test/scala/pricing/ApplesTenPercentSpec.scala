import org.scalatest.funsuite.AnyFunSuite
import pricing.OfferContext
import pricing.offers.ApplesTenPercent

class ApplesTenPercentSpec extends AnyFunSuite {
  private val ctx = OfferContext(
    counts = Map("apples" -> 2),
    unitPrices = Map("apples" -> 100)
  )

  test("applies 10% off on apples subtotal") {
    val offer = new ApplesTenPercent
    val d     = offer.evaluate(ctx).get
    assert(d.amount == 20)
    assert(d.label.contains("10%"))
  }

  test("no apples -> no discount") {
    val offer = new ApplesTenPercent
    assert(offer.evaluate(ctx.copy(counts = Map("apples" -> 0))).isEmpty)
  }
}
