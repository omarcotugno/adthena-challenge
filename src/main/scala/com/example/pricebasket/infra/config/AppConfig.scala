package com.example.pricebasket.infra.config

import com.typesafe.config.ConfigFactory
import com.example.pricebasket.core.catalogue.Catalogue
import com.example.pricebasket.core.pricing.{Offers, PriceCalculator}

object AppConfig {
  lazy val config = ConfigFactory.load()

  lazy val catalogue: Catalogue = ConfigCatalogue.from(config, "pricebasket")

  lazy val offers: Offers = {
    val rules = OffersLoader.load(config, "pricebasket")
    Offers(rules)
  }

  lazy val calculator = PriceCalculator(catalogue, offers.rules)
}
