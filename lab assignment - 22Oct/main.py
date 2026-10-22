from warehouse.models import Case, Client, Figurine, Warehouse


def main():
    bat_hero = Figurine("FIG-001", "Bat Hero")
    space_ranger = Figurine("FIG-002", "Space Ranger")

    case_a = Case("CASE-001")
    case_a.add_stock(bat_hero, 20)
    case_a.add_stock(space_ranger, 12)

    warehouse = Warehouse("WH-001", "Main Warehouse")
    warehouse.add_case(case_a)

    client = Client("CL-001", "Big Entertainment Co.")
    order = client.place_order("ORD-001", {bat_hero: 5, space_ranger: 3})

    print(f"Warehouse: {warehouse.name}")
    print("Inventory before shipment:")
    for figurine, quantity in warehouse.inventory().items():
        print(f"  {figurine.name} ({figurine.sku}): {quantity}")

    warehouse.fulfill_order(order)

    print(f"\nOrder {order.order_id} status: {order.status}")
    print("Inventory after shipment:")
    for figurine, quantity in warehouse.inventory().items():
        print(f"  {figurine.name} ({figurine.sku}): {quantity}")


if __name__ == "__main__":
    main()
