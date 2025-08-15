import org.scalatest.funsuite.AnyFunSuite
import domain.{Basket, Product}
import pricing.PricingService

class PricingServiceSpec extends AnyFunSuite {
  test("matches example: Apples Milk Bread") {
    val basket = Basket(List(Product.Apples, Product.Milk, Product.Bread))
    val svc    = PricingService.default
    val res    = svc.price(basket)

    assert(res.subtotal == (100 + 130 + 80)) // £3.10
    val discountSum = res.discounts.map(_.amount).sum
    assert(discountSum == 10) // 10% of £1.00 = 10p
    assert(res.total == 300)  // £3.00
  }

  test("soup offer stacks correctly") {
    val basket = Basket(List.fill(4)(Product.Soup) ++ List.fill(3)(Product.Bread))
    val svc    = PricingService.default
    val res    = svc.price(basket)
    // subtotal: 4*65 + 3*80 = 260 + 240 = 500
    // discount: eligible breads = min(4/2, 3)=2 -> 2*80/2 = 80
    assert(res.subtotal == 500)
    assert(res.discounts.map(_.amount).sum == 80)
    assert(res.total == 420)
  }

  test("no offers -> prints (No offers available)") {
    val basket = Basket(List(Product.Milk))
    val svc    = PricingService.default
    val res    = svc.price(basket)
    assert(res.subtotal == 130)
    assert(res.discounts.isEmpty)
    assert(res.total == 130)
  }
}
