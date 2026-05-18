// Glean Generated Code
package com.abhinav.orderprocessor.model;

import java.time.Instant;
import java.util.UUID;

public class OrderEvent {

    private String orderId;
    private String customerId;
    private String sku;
    private int quantity;
    private long priceCents;
    private long totalCents;
    private Instant createdAt;

    public static OrderEvent from(OrderRequest req) {
        OrderEvent e = new OrderEvent();
        e.orderId = UUID.randomUUID().toString();
        e.customerId = req.getCustomerId();
        e.sku = req.getSku();
        e.quantity = req.getQuantity();
        e.priceCents = req.getPriceCents();
        e.totalCents = (long) req.getQuantity() * req.getPriceCents();
        e.createdAt = Instant.now();
        return e;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public long getPriceCents() { return priceCents; }
    public long getTotalCents() { return totalCents; }
    public Instant getCreatedAt() { return createdAt; }
}
