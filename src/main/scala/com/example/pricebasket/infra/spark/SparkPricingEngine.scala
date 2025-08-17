package com.example.pricebasket.infra.spark

import org.apache.spark.sql.{Dataset, SparkSession}
import com.example.pricebasket.core.catalogue.Catalogue
import com.example.pricebasket.core.pricing.PriceCalculator
import com.example.pricebasket.core.model._

final class SparkPricingEngine(calculator: PriceCalculator, catalogue: Catalogue) {

  /** End-to-end: raw item strings -> normalised counts -> pricing. */
  def price(
      rawItems: Seq[String]
  )(implicit spark: SparkSession): Either[DomainError, PricingResult] = {
    import spark.implicits._

    val knownSet: Set[String] = catalogue.products.keySet

    val normalised: Seq[String]  = rawItems.map(catalogue.normalise)
    val itemsDS: Dataset[String] = spark.createDataset(normalised)

    val unknown: Array[String] = itemsDS
      .filter(n => !knownSet.contains(n))
      .distinct
      .collect()

    if (unknown.nonEmpty) {
      val known = catalogue.products.keys.toList
      return Left(DomainError.UnknownItems(unknown.toList, known))
    }

    val counts: Map[String, Long] = itemsDS
      .groupByKey(identity)
      .count()
      .collect()
      .map { case (name, cnt) => name -> cnt }
      .toMap

    calculator.price(counts)
  }
}
