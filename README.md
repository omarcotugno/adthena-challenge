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
PriceBasket Apples Milk Bread
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
sbt "run Soup Soup Bread"
# Subtotal: £2.10
# Buy 2 Soup, Bread half price: 40p
# Total price: £1.70
```

---

## Tech Stack
- Scala 2.13
- sbt
- ScalaTest (unit tests)
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
- sbt

> Check versions with `java -version` and `sbt --version`.

### Build, Test & Run
```bash
sbt compile        # compile sources
sbt test           # run unit tests
sbt "run <items>"  # run with items, e.g. sbt "run Apples Milk Bread"
```

**Notes:**
- When using sbt, wrap the `run` arguments in quotes.
- Unknown items are reported on stderr and result in a non‑zero exit code.

---

## Architecture (SOLID)

**Single Responsibility**
- Each class/file has one reason to change (e.g., individual offer logic in its own class).

**Open/Closed**
- New offers are added by implementing `pricing.Offer` and wiring in `PricingService.default` without modifying core pricing logic.

**Liskov Substitution**
- All offers conform to the `Offer` trait contract (`evaluate` returns an optional `Discount`).

**Interface Segregation**
- CLI, domain, pricing, and utilities are split into focused modules.

**Dependency Inversion**
- `PricingService` depends on the `Offer` abstraction, not concrete implementations.

### Key Types
- `util.Money` – integer pence math + formatting.
- `domain.Product` – catalog and parsing from CLI strings.
- `domain.Basket` – immutable collection + counting helpers.
- `pricing.Offer` / `pricing.Discount` – offer contract and discount value object.
- `pricing.PricingService` – orchestrates subtotal, applies offers, computes total.
- `cli.PriceBasketApp` – CLI adapter (I/O + exit codes).

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
│   ├── main/scala/
│   │   ├── Main.scala
│   │   ├── cli/PriceBasketApp.scala
│   │   ├── domain/{Basket.scala,Product.scala}
│   │   ├── pricing/{Offer.scala,PricingService.scala}
│   │   ├── pricing/offers/{ApplesTenPercent.scala,SoupBreadHalfPrice.scala}
│   │   └── util/Money.scala
│   └── test/scala/
│       ├── pricing/{ApplesTenPercentSpec.scala,SoupBreadHalfPriceSpec.scala,PricingServiceSpec.scala}
│       └── util/MoneySpec.scala
└── README.md
```

---

## Development Tips
- **Add a new offer:** create a class implementing `Offer` in `pricing/offers/`, then register it in `PricingService.default`.
- **Formatting:** Run `scalafmt` (if configured) to keep code style consistent.
- **Exit codes:** `PriceBasketApp.run` returns `0` on success, `2` on usage/parse errors.

---

## License
This code is provided solely for the purpose of the Adthena Data Engineer technical assignment.
All rights reserved.
