package com.example.pricebasket.core.pricing

import com.example.pricebasket.core.rules.DiscountRule

final case class Offers(rules: Seq[DiscountRule])
