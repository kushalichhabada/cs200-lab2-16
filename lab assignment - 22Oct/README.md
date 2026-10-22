# Warehouse Inventory System

A small object-oriented design for Company XYZ's warehouse inventory scenario.

## Core design
The recommended answer is **5 core classes**:
1. `Figurine`: identifies a figurine type.
2. `Case`: stores quantities of figurine types.
3. `Warehouse`: stores cases and calculates/updates inventory.
4. `Client`: places orders.
5. `Order`: records requested quantities and shipment status.

The code also uses `OrderLine` as a helper class to represent one product and quantity within an order. If every class declaration is counted, there are 6 classes including this helper. The diagram explains this distinction.

## Relationships
- A warehouse stores multiple cases.
- A case stores quantities of figurines.
- A client can place multiple orders.
- An order contains order lines.
- The warehouse fulfills an order by deducting stock from its cases.

See `docs/class-diagram.md`.

## Run
Requires Python 3.10 or newer. No external packages are required.

```bash
python main.py
```

Run tests with:

```bash
python -m unittest discover -s tests
```

This is a quick educational design. A production system would also need a database, authentication, shipment tracking, and transaction handling.
