from dataclasses import dataclass, field


@dataclass(frozen=True)
class Figurine:
    """Identifies one type of figurine."""
    sku: str
    name: str


@dataclass
class Case:
    """A physical case containing quantities of figurines."""
    case_id: str
    stock: dict = field(default_factory=dict)

    def add_stock(self, figurine, quantity):
        if quantity <= 0:
            raise ValueError("Quantity must be greater than zero.")
        self.stock[figurine] = self.stock.get(figurine, 0) + quantity

    def remove_stock(self, figurine, quantity):
        if quantity <= 0:
            raise ValueError("Quantity must be greater than zero.")
        available = self.stock.get(figurine, 0)
        if available < quantity:
            raise ValueError(f"Not enough {figurine.name} in case {self.case_id}.")
        self.stock[figurine] = available - quantity


@dataclass
class OrderLine:
    """A supporting class for one figurine and its requested quantity."""
    figurine: Figurine
    quantity: int

    def __post_init__(self):
        if self.quantity <= 0:
            raise ValueError("Order quantity must be greater than zero.")


@dataclass
class Order:
    """A client's order."""
    order_id: str
    client: object
    lines: list
    status: str = "PENDING"

    @property
    def items(self):
        return {line.figurine: line.quantity for line in self.lines}


@dataclass
class Client:
    """A customer who places orders."""
    client_id: str
    name: str
    orders: list = field(default_factory=list)

    def place_order(self, order_id, items):
        if not items:
            raise ValueError("An order must contain at least one item.")
        lines = [OrderLine(figurine, quantity) for figurine, quantity in items.items()]
        order = Order(order_id, self, lines)
        self.orders.append(order)
        return order


@dataclass
class Warehouse:
    """Stores cases and manages inventory and fulfillment."""
    warehouse_id: str
    name: str
    cases: list = field(default_factory=list)

    def add_case(self, case):
        if any(existing.case_id == case.case_id for existing in self.cases):
            raise ValueError(f"Case ID {case.case_id} already exists.")
        self.cases.append(case)

    def inventory(self):
        totals = {}
        for case in self.cases:
            for figurine, quantity in case.stock.items():
                totals[figurine] = totals.get(figurine, 0) + quantity
        return totals

    def fulfill_order(self, order):
        if order.status != "PENDING":
            raise ValueError(f"Order {order.order_id} is not pending.")

        # Validate the full order before changing any inventory.
        available = self.inventory()
        for figurine, requested in order.items.items():
            if available.get(figurine, 0) < requested:
                raise ValueError(
                    f"Insufficient stock for {figurine.name}: "
                    f"{available.get(figurine, 0)} available, {requested} requested."
                )

        # Deduct each requested quantity across cases as needed.
        for figurine, requested in order.items.items():
            remaining = requested
            for case in self.cases:
                take = min(case.stock.get(figurine, 0), remaining)
                if take:
                    case.remove_stock(figurine, take)
                    remaining -= take
                if remaining == 0:
                    break

        order.status = "SHIPPED"
