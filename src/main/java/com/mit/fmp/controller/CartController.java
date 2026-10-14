package com.mit.fmp.controller;


import com.mit.fmp.dto.CartItemResponse;
import com.mit.fmp.dto.CartRequest;
import com.mit.fmp.service.CartService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ADD PRODUCT TO CART
    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addToCart(
            @Valid @RequestBody CartRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addToCart(request));
    }

    // VIEW BUYER CART
    @GetMapping("/{buyerId}")
    public ResponseEntity<List<CartItemResponse>> getCart(
            @PathVariable Long buyerId) {

        return ResponseEntity.ok(
                cartService.getCart(buyerId));
    }

    // UPDATE QUANTITY
    @PutMapping("/{buyerId}/item/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @PathVariable Long buyerId,
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> body) {

        return ResponseEntity.ok(
                cartService.updateQuantity(
                        buyerId,
                        cartItemId,
                        body.get("quantity")));
    }

    // REMOVE ONE ITEM
    @DeleteMapping("/{buyerId}/item/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long buyerId,
            @PathVariable Long cartItemId) {

        cartService.removeItem(buyerId, cartItemId);

        return ResponseEntity.noContent().build();
    }

    // CLEAR BUYER CART
    @DeleteMapping("/{buyerId}/clear")
    public ResponseEntity<Void> clearCart(
            @PathVariable Long buyerId) {

        cartService.clearCart(buyerId);

        return ResponseEntity.noContent().build();
    }
}