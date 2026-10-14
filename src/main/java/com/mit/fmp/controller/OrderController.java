package com.mit.fmp.controller;



import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mit.fmp.service.OrderService;
import com.mit.fmp.dto.OrderRequest;
import com.mit.fmp.dto.OrderResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // PLACE ORDER
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(request));
    }
    
    // GET ORDER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // GET ALL ORDERS
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

   

    // GET BUYER ORDERS
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByBuyer(
            @PathVariable Long buyerId) {

        return ResponseEntity.ok(
                orderService.getOrdersByBuyer(buyerId));
    }

    // GET FARMER ORDERS
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByFarmer(
            @PathVariable Long farmerId) {

        return ResponseEntity.ok(
                orderService.getOrdersByFarmer(farmerId));
    }

    // UPDATE ORDER STATUS
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");

        if (status == null || status.isBlank()) {
            throw new org.springframework.web.server
                    .ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required");
        }

        return ResponseEntity.ok(
                orderService.updateOrderStatus(id, status));
    }
}
