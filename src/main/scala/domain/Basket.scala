package domain

final case class Basket(items: List[Product]) {
  def count(p: Product): Int = items.count(_ == p)
}
object Basket                                 {
  def empty: Basket = Basket(Nil)
}
