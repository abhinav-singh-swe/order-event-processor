// Glean Generated Code
package com.abhinav.orderprocessor;

import com.abhinav.orderprocessor.model.OrderEvent;
import com.abhinav.orderprocessor.model.OrderRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderEventTest {

    @Test
    void totalCents_isQuantityTimesPrice() {
        OrderRequest req = new OrderRequest();
        req.setCustomerId("C001");
        req.setSku("SKU-42");
        req.setQuantity(3);
        req.setPriceCents(1999L);

        OrderEvent event = OrderEvent.from(req);

        assertNotNull(event.getOrderId());
        assertEquals(5997L, event.getTotalCents());
        assertEquals("C001", event.getCustomerId());
    }
}
