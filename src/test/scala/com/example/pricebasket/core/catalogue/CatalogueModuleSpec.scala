package com.example.pricebasket.core.catalogue

import org.scalatest.funsuite.AnyFunSuite
import com.typesafe.config.ConfigFactory
import com.example.pricebasket.infra.config.ConfigCatalogue

class CatalogueModuleSpec extends AnyFunSuite {

  private val conf = ConfigFactory.parseString(
    """
      |pricebasket {
      |  products = [
      |    { name = "Soup",   price = 0.65 },
      |    { name = "Bread",  price = 0.80 },
      |    { name = "Milk",   price = 1.30 },
      |    { name = "Apples", price = 1.00 }
      |  ]
      |  normaliser = [
      |    { from = ["soup","soups","  soup  "], to = "Soup" },
      |    { from = ["bread","BREAD"],           to = "Bread" },
      |    { from = ["milk","MILK"],             to = "Milk" },
      |    { from = ["apple","apples","APPLE"],  to = "Apples" }
      |  ]
      |}
      |""".stripMargin)

  private val cat: Catalogue = ConfigCatalogue.from(conf, "pricebasket")

  test("products are loaded with expected prices") {
    assert(cat.products.keySet == Set("Soup","Bread","Milk","Apples"))
    assert(cat.products("Soup").price.toString == "0.65")
    assert(cat.products("Bread").price.toString == "0.80")
    assert(cat.products("Milk").price.toString == "1.30")
    assert(cat.products("Apples").price.toString == "1.00")
  }

  test("normalise maps aliases, trims spaces, and applies capitalization fallback") {
    assert(cat.normalise("soup")    == "Soup")
    assert(cat.normalise("soups")   == "Soup")
    assert(cat.normalise("  soup  ")== "Soup")
    assert(cat.normalise("BREAD")   == "Bread")
    assert(cat.normalise("milk")    == "Milk")
    assert(cat.normalise("APPLE")   == "Apples")

    // fallback: unknown strings are capitalized first-letter
    assert(cat.normalise("oranges") == "Oranges")
  }

  test("isKnown reflects product table") {
    assert(cat.isKnown("Soup"))
    assert(!cat.isKnown("Oranges"))
  }

  test("works even if 'normaliser' is missing: default capitalization applies") {
    val confNoAliases = ConfigFactory.parseString(
      """
        |pricebasket {
        |  products = [
        |    { name = "Tomatoes", price = 0.55 }
        |  ]
        |}
        |""".stripMargin)
    val cat2: Catalogue = ConfigCatalogue.from(confNoAliases, "pricebasket")
    // no aliases: fallback capitalization
    assert(cat2.normalise("tomatoes") == "Tomatoes")
    assert(cat2.isKnown("Tomatoes"))
  }
}
