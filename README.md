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

---

## Tech Stack
- Scala
- SBT (Scala Build Tool)
- Unit Testing (ScalaTest or Specs2)

---

## Getting Started

### Using VS Code Dev Container

This repository includes a `.devcontainer` setup for Scala and Spark development. You can open the project in Visual Studio Code and choose **Reopen in Container** to automatically get Java 17, Scala, sbt, and scalafmt installed and configured.

Steps:
1. Install [Docker](https://www.docker.com/get-started) and [VS Code](https://code.visualstudio.com/) with the [Dev Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers).
2. Clone this repository and open it in VS Code.
3. Select **Reopen in Container** when prompted.
4. Wait for the container to build and open a terminal inside the container environment.

### Prerequisites

If you are not using the VS Code Dev Container, you will need to have the following installed on your system:

- [Scala](https://www.scala-lang.org/download/)
- [sbt (Scala Build Tool)](https://www.scala-sbt.org/download.html)

### Build & Run

You can run the following commands inside the dev container terminal or on your local machine if you have the prerequisites installed.

#### Compile
```bash
sbt compile
```

#### Run
```bash
sbt "run <items>"
```

**Note:** When using sbt, quotes are needed around the arguments (e.g., `"run Apples Milk Bread"`) to avoid parsing issues.

#### Example
```bash
sbt "run Apples Milk Bread"
```

#### Test
```bash
sbt test
```

---

## Repository Structure

```bash
.
├── .devcontainer
│   ├── devcontainer.json
│   └── Dockerfile
├── .vscode
│   ├── extensions.json
│   └── settings.json
├── .scalafmt.conf
├── .gitignore
├── project
│   └── plugins.sbt
├── LICENSE
├── src
│   ├── main
│   │   └── scala
│   └── test
│       └── scala
├── build.sbt
└── README.md
```

---

## License

This code is provided solely for the purpose of completing the Adthena Data Engineer technical assignment.
All rights reserved.
