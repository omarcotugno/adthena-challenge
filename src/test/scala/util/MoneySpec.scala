import org.scalatest.funsuite.AnyFunSuite
import util.Money

class MoneySpec extends AnyFunSuite {
  test("format under a pound") {
    assert(Money.format(65) == "65p")
    assert(Money.format(5) == "5p")
  }
  test("format one pound and above") {
    assert(Money.format(100) == "£1.00")
    assert(Money.format(130) == "£1.30")
  }
  test("percent rounds half up") {
    assert(Money.percent(100, 10) == 10)
    assert(Money.percent(65, 50) == 33) // 32.5 -> 33
  }
}
