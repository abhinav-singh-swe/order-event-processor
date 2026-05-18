// Glean Generated Code
package com.abhinav.orderprocessor.api;

import com.abhinav.orderprocessor.model.OrderEvent;
import com.abhinav.orderprocessor.model.OrderRequest;
import com.abhinav.orderprocessor.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody OrderRequest request) {
        OrderEvent event = orderService.accept(request);
        log.info("accepted order id={} customer={} sku={}",
                 event.getOrderId(), event.getCustomerId(), event.getSku());
        return ResponseEntity.accepted().body(Map.of("orderId", event.getOrderId()));
    }
}
