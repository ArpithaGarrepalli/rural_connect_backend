package com.ruralconnect.backend.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ruralconnect.backend.dto.OrderResponse;
import com.ruralconnect.backend.entity.Order;
import com.ruralconnect.backend.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestParam Long customerId,
                                        @RequestParam Long cropId,
                                        @RequestParam BigDecimal quantityKg) {
        try {
            Order order = orderService.placeOrder(customerId, cropId, quantityKg);
            return ResponseEntity.ok(Map.of(
                    "message", "Order placed successfully",
                    "order", new OrderResponse(order)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponse> getCustomerOrders(@PathVariable Long customerId) {
        return orderService.getCustomerOrders(customerId).stream()
                .map(OrderResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/farmer/{farmerId}")
    public List<OrderResponse> getFarmerOrders(@PathVariable Long farmerId) {
        return orderService.getFarmerOrders(farmerId).stream()
                .map(OrderResponse::new).collect(Collectors.toList());
    }

    @PostMapping("/{orderId}/cancel-request")
    public ResponseEntity<?> requestCancellation(@PathVariable Long orderId,
                                                  @RequestParam Long customerId,
                                                  @RequestBody Map<String, String> body) {
        try {
            Order order = orderService.requestCancellation(customerId, orderId, body.get("reason"));
            return ResponseEntity.ok(Map.of(
                    "message", "Cancellation request sent to admin",
                    "order", new OrderResponse(order)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
