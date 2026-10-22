# Class Diagram

```mermaid
classDiagram
    class Figurine {
        +sku
        +name
    }
    class Case {
        +case_id
        +stock
        +add_stock(figurine, quantity)
        +remove_stock(figurine, quantity)
    }
    class Warehouse {
        +warehouse_id
        +name
        +cases
        +add_case(case)
        +inventory()
        +fulfill_order(order)
    }
    class Client {
        +client_id
        +name
        +orders
        +place_order(order_id, items)
    }
    class Order {
        +order_id
        +client
        +lines
        +status
        +items
    }
    class OrderLine {
        +figurine
        +quantity
    }

    Warehouse "1" o-- "*" Case : stores
    Case "*" --> "*" Figurine : stocks
    Client "1" --> "*" Order : places
    Order "1" *-- "*" OrderLine : contains
    OrderLine "*" --> "1" Figurine : requests
```

## Why these classes?
- `Figurine`: product identity.
- `Case`: physical storage and per-case quantities.
- `Warehouse`: inventory totals and order fulfillment.
- `Client`: customer details and order history.
- `Order`: requested products and shipment status.
- `OrderLine`: helper class for an item and quantity. If the instructor strictly requires only five classes, order lines can be stored as a dictionary inside `Order` instead.
