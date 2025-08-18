package com.example.pricebasket.infra.config

import com.typesafe.config.{Config, ConfigException}
import com.example.pricebasket.core.rules._
import scala.collection.JavaConverters._
import java.time.LocalDate
import java.time.format.DateTimeParseException

object OffersLoader {
  def load(conf: Config, rootPath: String = "pricebasket"): Seq[DiscountRule] = {
    val root = conf.getConfig(rootPath)
    if (!root.hasPath("offers")) return Seq.empty

    root.getConfigList("offers").asScala.toSeq.map { c =>
      // helper to read optional ISO local dates
      def optDate(path: String): Option[LocalDate] =
        if (c.hasPath(path)) {
          val s = c.getString(path).trim
          try Some(LocalDate.parse(s))
          catch {
            case _: DateTimeParseException =>
              throw new ConfigException.BadValue(path, s"Invalid date '$s' (expected YYYY-MM-DD)")
          }
        } else None

      val start = optDate("startDate")
      val end   = optDate("endDate")
      // validate window if both present
      (start, end) match {
        case (Some(s), Some(e)) if s.isAfter(e) =>
          throw new ConfigException.BadValue(
            "pricebasket.offers",
            s"startDate $s is after endDate $e"
          )
        case _                                  => // ok
      }

      c.getString("type").trim.toLowerCase match {
        case "percentage-per-item" =>
          val product = c.getString("product").trim
          val pct     = BigDecimal(c.getDouble("pct"))
          val label   =
            if (c.hasPath("label")) c.getString("label")
            else s"$product ${(pct * 100).bigDecimal.stripTrailingZeros.toPlainString}% off"
          PercentageOffPerItem(product, pct, label, startDate = start, endDate = end)

        case "linked-multibuy" =>
          val reqProd  = c.getString("requiredProduct").trim
          val reqQty   = c.getInt("requiredQty")
          val discProd = c.getString("discountedProduct").trim
          val discPct  = BigDecimal(c.getDouble("discountPct"))
          val label    =
            if (c.hasPath("label")) c.getString("label")
            else
              s"$discProd ${(discPct * 100).bigDecimal.stripTrailingZeros.toPlainString}% off (with $reqQty $reqProd)"
          LinkedMultiBuyDiscount(
            reqProd,
            reqQty,
            discProd,
            discPct,
            label,
            startDate = start,
            endDate = end
          )

        case other =>
          throw new ConfigException.BadValue("pricebasket.offers", s"Unsupported rule type: $other")
      }
    }
  }
}
