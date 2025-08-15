package domain

import util.Money.Pence
import util.Money._

sealed trait Product {
  def code: String
  def name: String
  def unitPrice: Pence
}
object Product       {
  case object Soup   extends Product { val code = "soup"; val name = "Soup"; val unitPrice = 65   }
  case object Bread  extends Product { val code = "bread"; val name = "Bread"; val unitPrice = 80 }
  case object Milk   extends Product {
    val code = "milk"; val name = "Milk"; val unitPrice = fromPounds(1.30)
  }
  case object Apples extends Product {
    val code = "apples"; val name = "Apples"; val unitPrice = fromPounds(1.00)
  }

  val all: List[Product] = List(Soup, Bread, Milk, Apples)

  def fromString(s: String): Either[String, Product] = {
    val normalized = s.trim.toLowerCase
    all
      .find(p => p.code == normalized || p.name.toLowerCase == normalized)
      .toRight(s"Unknown item: $s")
  }
}
