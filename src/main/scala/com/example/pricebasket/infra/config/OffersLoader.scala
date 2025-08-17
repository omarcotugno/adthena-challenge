package com.example.pricebasket.infra.config

import com.typesafe.config.{Config, ConfigException}
import com.example.pricebasket.core.rules._
import scala.collection.JavaConverters._

/** Parses `pricebasket.offers` into Seq[DiscountRule].
  *
  * Supported entries: { type = "percentage-per-item", product = "Apples", pct = 0.10, label =
  * "Apples 10% off" } { type = "linked-multibuy", requiredProduct = "Soup", requiredQty = 2,
  * discountedProduct = "Bread", discountPct = 0.50, label = "Bread 50% off (with 2 Soup)" }
  */
object OffersLoader {
  def load(conf: Config, rootPath: String = "pricebasket"): Seq[DiscountRule] = {
    val root = conf.getConfig(rootPath)
    if (!root.hasPath("offers")) return Seq.empty

    root.getConfigList("offers").asScala.toSeq.map { c =>
      val tpe = c.getString("type").trim.toLowerCase
      tpe match {
        case "percentage-per-item" =>
          val product = c.getString("product").trim
          val pct     = BigDecimal(c.getDouble("pct"))
          val label   =
            if (c.hasPath("label")) c.getString("label")
            else s"$product ${(pct * 100).bigDecimal.stripTrailingZeros.toPlainString}% off"
          PercentageOffPerItem(product, pct, label)

        case "linked-multibuy" =>
          val reqProd  = c.getString("requiredProduct").trim
          val reqQty   = c.getInt("requiredQty")
          val discProd = c.getString("discountedProduct").trim
          val discPct  = BigDecimal(c.getDouble("discountPct"))
          val label    =
            if (c.hasPath("label")) c.getString("label")
            else
              s"$discProd ${(discPct * 100).bigDecimal.stripTrailingZeros.toPlainString}% off (with $reqQty $reqProd)"
          LinkedMultiBuyDiscount(reqProd, reqQty, discProd, discPct, label)

        case other =>
          throw new ConfigException.BadValue("pricebasket.offers", s"Unsupported rule type: $other")
      }
    }
  }
}
