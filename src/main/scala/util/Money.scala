package util

object Money {
  type Pence = Int

  def fromPounds(pounds: BigDecimal): Pence =
    pounds.bigDecimal.movePointRight(2).setScale(0, java.math.RoundingMode.HALF_UP).intValue

  def percent(amount: Pence, pct: BigDecimal): Pence =
    (BigDecimal(amount) * pct / 100).setScale(0, BigDecimal.RoundingMode.HALF_UP).toInt

  def format(amount: Pence): String = {
    val sign = if (amount < 0) "-" else ""
    val abs  = math.abs(amount)
    if (abs < 100) s"${sign}${abs}p"
    else {
      val pounds = abs / 100
      val pence  = abs % 100
      s"${sign}£%d.%02d".format(pounds, pence)
    }
  }
}
