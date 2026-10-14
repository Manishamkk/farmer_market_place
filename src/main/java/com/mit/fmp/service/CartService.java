package com.mit.fmp.service;


import com.mit.fmp.dto.CartItemResponse;
import com.mit.fmp.dto.CartRequest;

import java.util.List;

public interface CartService {

    CartItemResponse addToCart(CartRequest request);

    List<CartItemResponse> getCart(Long buyerId);

    CartItemResponse updateQuantity(
            Long buyerId, Long cartItemId, Integer quantity);

    void removeItem(Long buyerId, Long cartItemId);

    void clearCart(Long buyerId);
}