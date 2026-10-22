import unittest

from warehouse.models import Case, Client, Figurine, Warehouse


class WarehouseTests(unittest.TestCase):
    def setUp(self):
        self.figure = Figurine("FIG-1", "Test Figure")
        self.case = Case("CASE-1")
        self.case.add_stock(self.figure, 10)
        self.warehouse = Warehouse("WH-1", "Test Warehouse")
        self.warehouse.add_case(self.case)
        self.client = Client("CL-1", "Test Client")

    def test_inventory_counts_stock(self):
        self.assertEqual(self.warehouse.inventory()[self.figure], 10)

    def test_fulfill_order_reduces_stock(self):
        order = self.client.place_order("ORD-1", {self.figure: 4})
        self.warehouse.fulfill_order(order)
        self.assertEqual(order.status, "SHIPPED")
        self.assertEqual(self.warehouse.inventory()[self.figure], 6)

    def test_insufficient_stock_does_not_change_inventory(self):
        order = self.client.place_order("ORD-2", {self.figure: 11})
        with self.assertRaises(ValueError):
            self.warehouse.fulfill_order(order)
        self.assertEqual(self.warehouse.inventory()[self.figure], 10)
        self.assertEqual(order.status, "PENDING")


if __name__ == "__main__":
    unittest.main()
