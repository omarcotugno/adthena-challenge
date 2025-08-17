package com.example.pricebasket.infra.config

import com.typesafe.config.Config
import com.example.pricebasket.core.catalogue.Catalogue
import com.example.pricebasket.core.model.Product
import com.example.pricebasket.core.money.Money

import scala.collection.JavaConverters._

/** Catalogue backed by Typesafe Config.
  *
  * Expects: pricebasket.products = [ { name = "Soup", price = 0.65 }, ... ] pricebasket.normaliser
  * \= [ { from = ["soup","soups"], to = "Soup" }, ... ]
  */
final class ConfigCatalogue private (
    val products: Map[String, Product],
    private val aliases: Map[String, String]
) extends Catalogue {
  override def normalise(raw: String): String =
    aliases.getOrElse(raw.trim.toLowerCase, raw.trim.capitalize)
}

object ConfigCatalogue {
  def from(conf: Config, rootPath: String = "pricebasket"): ConfigCatalogue = {
    val root = conf.getConfig(rootPath)

    val productsList = root.getConfigList("products").asScala.toList.map { c =>
      val name  = c.getString("name").trim
      val price = Money.gbp(BigDecimal(c.getDouble("price")))
      name -> Product(name, price)
    }
    val products     = productsList.toMap

    val aliasPairs: List[(String, String)] =
      if (root.hasPath("normaliser")) {
        root.getConfigList("normaliser").asScala.toList.flatMap { c =>
          val to   = c.getString("to").trim
          val from = c.getStringList("from").asScala.toList.map(_.trim.toLowerCase)
          from.map(_ -> to)
        }
      } else Nil

    new ConfigCatalogue(products, aliasPairs.toMap)
  }
}
