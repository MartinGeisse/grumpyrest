package name.martingeisse.grumpyrest_demo;

// not linked to a Product anymore because the Product can change, but the OrderLineItem is unaffected by that
public record OrderLineItem(int orderId, int quantity, String name, int unitPrice) {
}
