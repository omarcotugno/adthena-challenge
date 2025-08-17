package com.example.pricebasket.core.model

sealed trait DomainError
object DomainError {
  final case class UnknownItems(items: List[String], known: List[String]) extends DomainError
  case object EmptyBasket                                                 extends DomainError
}
