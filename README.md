# Adthena - Data Engineer Technical Assignment 2025

## Overview
Scala/SBT solution for the **Shopping Basket** problem, implemented with SOLID principles and full unit-test coverage. The app accepts items from the command line, computes the subtotal, applies special offers, and prints discounts and the final total.

### Goods & Prices
| Item   | Price |
|--------|-------|
| Soup   | £0.65 per tin |
| Bread  | £0.80 per loaf |
| Milk   | £1.30 per bottle |
| Apples | £1.00 per bag |

### Current Offers
1. **Apples 10% off** (per-apples subtotal).
2. **Buy 2 Soups, Bread half price** (for every 2 soups, up to one bread at 50% off).

---

## Example Usage

**Input:**
```bash
sbt "runSpark Apples Milk Bread"
```
**Output**:
```bash
Subtotal: £3.10
Apples 10% off: 10p
Total price: £3.00
```
No offer example:
```bash
Subtotal: £1.30
(No offers available)
Total price: £1.30
```
Soup/bread offer example:
```bash
sbt "runSpark Soup Soup Bread"
Subtotal: £2.10
Buy 2 Soup, Bread half price: 40p
Total price: £1.70
```

---

## Tech Stack
- Scala 2.13
- sbt
- ScalaTest (unit tests)
- Spark 3.5.x
- VS Code Dev Container (Java 17, Scala, sbt, scalafmt)

---

## Getting Started

### Using VS Code Dev Container (recommended)
This repo includes a `.devcontainer/` for a ready-to-code environment.
1. Install **Docker** and **VS Code** + **Dev Containers** extension.
2. Open the repository folder in VS Code.
3. When prompted, choose **Reopen in Container**.
4. Open a terminal inside the container.

### Prerequisites (without Dev Container)
- Java 17+
- Scala 2.13
- Spark 3.5.x
- sbt

> Check versions with `java -version` and `sbt --version`.

### Build, Test & Run
```bash
sbt compile        # compile sources
sbt test           # run unit tests
sbt "runSpark <items>"  # run with items, e.g. sbt "run Apples Milk Bread"
```

**Notes:**
- When using sbt, wrap the `runSpark` arguments in quotes.
- Unknown items are reported on stderr and result in a non‑zero exit code.

---

## Architecture (SOLID)

**Single Responsibility**
- Each package has one reason to change (e.g., `core.rules` for offer logic, `core.catalogue` for product lookup, `infra.spark` for Spark integration).

**Open/Closed**
- New offers are added by creating a new class in `core.rules` (implementing `DiscountRule`) and wiring it via `core.pricing.Offers`, without modifying existing pricing logic.
- New products are added via configuration in `application.conf`, not by changing code.

**Liskov Substitution**
- All rules conform to the `DiscountRule` trait contract (`apply` takes item counts and returns `Seq[DiscountLine]`), so any new rule can substitute an existing one.

**Interface Segregation**
- Interfaces are small and focused: e.g. `Catalogue` for product lookup/normalisation, `DiscountRule` for offers, `PriceCalculator` for totals. Clients depend only on what they use.

**Dependency Inversion**
- High-level modules (`PriceBasketApp`, `SparkPricingEngine`) depend on abstractions (`Catalogue`, `DiscountRule`) and are wired at runtime via `infra.config.AppConfig`. Core logic does not depend on configuration or Spark.

```
         +---------------+
         |      app      |
         | (CLI entrypoint)
         +-------+-------+
                 |
                 v
         +---------------+
         |     infra     |
         | (config, spark)|
         +-------+-------+
                 |
                 v
         +---------------+
         |     core      |
         | (business logic)|
         +---------------+
```

- **app**: User interface and entrypoint, handles CLI input/output.
- **infra**: Infrastructure layer, configuration, and Spark integration.
- **core**: Core business logic, domain model, pricing rules.

### Key Types
- `core.money.Money` – rounding & GBP helpers.
- `core.model.{Product, DiscountLine, PricingResult, DomainError}` – domain value objects & errors.
- `core.catalogue.Catalogue` – trait for product lookup & normalisation; implemented by `infra.config.ConfigCatalogue`.
- `core.rules.DiscountRule` – offer contract; concrete rules: `PercentageOffPerItem`, `LinkedMultiBuyDiscount` (+ optional `RuleCombinators`).
- `core.pricing.{Offers, PriceCalculator}` – holds active rules and computes subtotal/discounts/total.
- `infra.config.{AppConfig, OffersLoader}` – wiring from `application.conf` to runtime objects.
- `infra.spark.SparkPricingEngine` – Spark adapter: strings → counts → calculator.
- `app.PriceBasketApp` – CLI entrypoint.

---

## Repository Structure

```bash
.
├── .devcontainer/
│   ├── devcontainer.json
│   └── Dockerfile
├── .vscode/
│   ├── extensions.json
│   └── settings.json
├── build.sbt
├── project/
│   └── plugins.sbt
├── src/
│   ├── main/
│   │   ├── resources/
│   │   │   └── application.conf
│   │   └── scala/com/example/pricebasket/
│   │       ├── app/
│   │       │   └── PriceBasketApp.scala
│   │       ├── core/
│   │       │   ├── money/Money.scala
│   │       │   ├── model/
│   │       │   │   ├── Product.scala
│   │       │   │   ├── DiscountLine.scala
│   │       │   │   ├── PricingResult.scala
│   │       │   │   └── DomainError.scala
│   │       │   ├── catalogue/
│   │       │   │   ├── Catalogue.scala
│   │       │   │   └── Normaliser.scala
│   │       │   ├── rules/
│   │       │   │   ├── DiscountRule.scala
│   │       │   │   ├── PercentageOffPerItem.scala
│   │       │   │   ├── LinkedMultiBuyDiscount.scala
│   │       │   │   └── RuleCombinators.scala
│   │       │   └── pricing/
│   │       │       ├── Offers.scala
│   │       │       └── PriceCalculator.scala
│   │       └── infra/
│   │           ├── config/
│   │           │   ├── AppConfig.scala
│   │           │   ├── ConfigCatalogue.scala
│   │           │   └── OffersLoader.scala
│   │           └── spark/
│   │               └── SparkPricingEngine.scala
│   └── test/scala/com/example/pricebasket/
│       ├── core/
│       │   ├── money/MoneySpec.scala
│       │   ├── model/PricingResultSpec.scala
│       │   ├── catalogue/CatalogueModuleSpec.scala
│       │   ├── rules/
│       │   │   ├── PercentageOffPerItemSpec.scala
│       │   │   └── LinkedMultiBuyDiscountSpec.scala
│       │   └── pricing/PriceCalculatorSpec.scala
│       └── infra/
│           ├── config/{ConfigCatalogueSpec.scala,OffersLoaderSpec.scala}
│           └── spark/SparkPricingEngineSpec.scala
└── README.md
```

---

## Development Tips
- **Add a new offer:** create a class implementing `Offer` in `pricing/offers/`, then register it in `PricingService.default`.
- **Formatting:** Run `scalafmt` (if configured) to keep code style consistent.
- **Exit codes:** `PriceBasketApp.run` returns `0` on success, `2` on usage/parse errors.

---

## Extending the Catalogue & Offers

You can easily extend the product catalogue and offers without modifying core logic, thanks to the SOLID design principles. Here’s how:

### Product & Normalisation Configuration

Products and their normalisation rules are defined in the `application.conf` file using HOCON format. This allows flexible mapping of input strings to canonical product names and setting prices.

Example snippet from `application.conf`:

```hocon
products {
  Apples = 100       # price in pence
  Bread = 80
  Milk = 130
  Soup = 65
}

normaliser {
  Apples = ["apple", "apples", "appl"]
  Bread = ["bread", "loaf"]
  Milk = ["milk", "bottle"]
  Soup = ["soup", "tin"]
}
```

### How to Add a New Product

- Add a new entry under the `products` section with the product name as the key and its price in pence as the value.
- Add corresponding normalisation aliases under the `normaliser` section to map various input strings to the product.

Example:

```hocon
products {
  Chocolate = 150
}

normaliser {
  Chocolate = ["choc", "chocolate", "chocbar"]
}
```

### How to Add Normalisation Aliases

- Under the `normaliser` section, add or extend the array of aliases for an existing product.
- This helps the CLI accept various user input forms mapping to the canonical product.

### How to Add New Offers

- Create a new class in `pricing/offers/` implementing the `Offer` trait.
- Implement the `evaluate` method to define the discount logic.
- Register your new offer in the `PricingService.default` list so it is applied during pricing.

### Summary

- Products and aliases are configured declaratively via `application.conf`.
- Offers are added via new classes implementing `Offer` and registered in `PricingService`.
- This design enables adding new products and offers without changing core pricing or domain logic.
- Remember to update or add unit tests for any new products or offers to maintain coverage.

---

## License
This code is provided solely for the purpose of the Adthena Data Engineer technical assignment.
All rights reserved.
