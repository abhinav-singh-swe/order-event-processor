// Glean Generated Code
package com.abhinav.orderconsumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRecord {
    public String orderId;
    public String customerId;
    public String sku;
    public int quantity;
    public long priceCents;
    public long totalCents;
    public Instant createdAt;
}
