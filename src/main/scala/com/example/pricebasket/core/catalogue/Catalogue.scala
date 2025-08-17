package com.example.pricebasket.core.catalogue

import com.example.pricebasket.core.model.Product

trait Catalogue {
  def products: Map[String, Product]
  def normalise(raw: String): String
  def isKnown(name: String): Boolean = products.contains(name)
}
