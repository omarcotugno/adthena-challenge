import org.scalatest.funsuite.AnyFunSuite
import pricing.OfferContext
import pricing.offers.SoupBreadHalfPrice

class SoupBreadHalfPriceSpec extends AnyFunSuite {
  private val unit = Map("soup" -> 65, "bread" -> 80)

  test("2 soups and 1 bread -> half price bread") {
    val offer = new SoupBreadHalfPrice
    val ctx   = OfferContext(Map("soup" -> 2, "bread" -> 1), unit)
    assert(offer.evaluate(ctx).get.amount == 40)
  }

  test("4 soups and 3 breads -> discount for 2 breads") {
    val offer = new SoupBreadHalfPrice
    val ctx   = OfferContext(Map("soup" -> 4, "bread" -> 3), unit)
    assert(offer.evaluate(ctx).get.amount == 80) // 2 * 40
  }

  test("insufficient soup or zero bread -> no discount") {
    val offer = new SoupBreadHalfPrice
    assert(offer.evaluate(OfferContext(Map("soup" -> 1, "bread" -> 1), unit)).isEmpty)
    assert(offer.evaluate(OfferContext(Map("soup" -> 2, "bread" -> 0), unit)).isEmpty)
  }
}
