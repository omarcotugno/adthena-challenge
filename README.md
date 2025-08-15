# Adthena - Data Engineer Technical Assignment 2025

## Overview
This project is a Scala-based solution for the **Shopping Basket** problem as part of the Adthena Data Engineer technical assignment.

The program:
- Accepts a list of items in a basket via the command line.
- Calculates the subtotal.
- Applies special offers where applicable.
- Displays the discounts and the final total price.

### Goods & Prices
| Item   | Price  |
|--------|--------|
| Soup   | £0.65 per tin |
| Bread  | £0.80 per loaf |
| Milk   | £1.30 per bottle |
| Apples | £1.00 per bag |

### Current Offers
1. Apples have **10% off** their normal price this week.  
2. Buy **2 tins of soup** and get a loaf of bread for **half price**.

---

## Example Usage

**Input:**
```bash
PriceBasket Apples Milk Bread

Output:

Subtotal: £3.10
Apples 10% off: 10p
Total price: £3.00

No offer example:

Subtotal: £1.30
(No offers available)
Total price: £1.30


⸻

Tech Stack
	•	Scala
	•	SBT (Scala Build Tool)
	•	Unit Testing (ScalaTest or Specs2)

⸻

Getting Started

Prerequisites
	•	Scala
	•	SBT

Build & Run

(Instructions to be completed after implementation)

# Compile
sbt compile

# Run
sbt "run <items>"

# Example
sbt "run Apples Milk Bread"

# Test
sbt test


⸻

Repository Structure

.
├── src
│   ├── main
│   │   └── scala    # Application source code
│   └── test
│       └── scala    # Unit tests
├── build.sbt        # SBT build configuration
└── README.md        # This file


⸻

License

This code is provided solely for the purpose of completing the Adthena Data Engineer technical assignment.
All rights reserved.
