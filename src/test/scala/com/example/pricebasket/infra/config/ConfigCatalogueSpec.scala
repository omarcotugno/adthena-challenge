package com.example.pricebasket.infra.config

import org.scalatest.funsuite.AnyFunSuite
import com.typesafe.config.ConfigFactory

class ConfigCatalogueSpec extends AnyFunSuite {
  private val conf = ConfigFactory.parseString(
    """
      |pricebasket {
      |  products = [
      |    { name = "Soup",   price = 0.65 },
      |    { name = "Bread",  price = 0.80 }
      |  ]
      |  normaliser = [
      |    { from = ["soup","soups"], to = "Soup" },
      |    { from = ["bread"], to = "Bread" }
      |  ]
      |}
      |""".stripMargin)

  test("loads products and aliases from config") {
    val cat = ConfigCatalogue.from(conf, "pricebasket")
    assert(cat.products.contains("Soup"))
    assert(cat.products("Bread").price.toString == "0.80")
    assert(cat.normalise("soups") == "Soup")
  }
}
