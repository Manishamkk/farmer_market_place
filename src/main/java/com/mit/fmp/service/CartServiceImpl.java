package com.mit.fmp.service;


import com.mit.fmp.dto.CartItemResponse;
import com.mit.fmp.dto.CartRequest;
import com.mit.fmp.entity.CartItem;
import com.mit.fmp.entity.Product;
import com.mit.fmp.entity.Users;
import com.mit.fmp.repository.CartRepository;
import com.mit.fmp.repository.ProductRepository;
import com.mit.fmp.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private Users getBuyer(Long buyerId) {
        Users buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Buyer not found"));

        if (!"BUYER".equalsIgnoreCase(buyer.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only buyers can use the cart");
        }

        return buyer;
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));
    }

    private CartItemResponse toResponse(CartItem item) {
        BigDecimal price = item.getProduct().getPrice();

        BigDecimal subtotal = price.multiply(
                BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getId(),
                item.getBuyer().getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                price,
                item.getQuantity(),
                subtotal
        );
    }

    @Override
    @Transactional
    public CartItemResponse addToCart(CartRequest request) {

        Users buyer = getBuyer(request.getBuyerId());
        Product product = getProduct(request.getProductId());

        if (request.getQuantity() == null
                || request.getQuantity() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be at least 1");
        }

        CartItem item = cartRepository
                .findByBuyer_IdAndProduct_Id(
                        buyer.getId(), product.getId())
                .orElse(null);

        int newQuantity = request.getQuantity();

        if (item != null) {
            newQuantity += item.getQuantity();
        }

        if (product.getQuantity() == null
                || newQuantity > product.getQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity exceeds available stock");
        }

        if (item == null) {
            item = new CartItem();
            item.setBuyer(buyer);
            item.setProduct(product);
        }

        item.setQuantity(newQuantity);

        return toResponse(cartRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long buyerId) {

        getBuyer(buyerId);

        return cartRepository.findByBuyer_Id(buyerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CartItemResponse updateQuantity(
            Long buyerId, Long cartItemId, Integer quantity) {

        getBuyer(buyerId);

        if (quantity == null || quantity < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be at least 1");
        }

        CartItem item = cartRepository
                .findByIdAndBuyer_Id(cartItemId, buyerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cart item not found"));

        Product product = item.getProduct();

        if (product.getQuantity() == null
                || quantity > product.getQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity exceeds available stock");
        }

        item.setQuantity(quantity);

        return toResponse(cartRepository.save(item));
    }

    @Override
    @Transactional
    public void removeItem(Long buyerId, Long cartItemId) {

        getBuyer(buyerId);

        CartItem item = cartRepository
                .findByIdAndBuyer_Id(cartItemId, buyerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cart item not found"));

        cartRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearCart(Long buyerId) {

        getBuyer(buyerId);
        cartRepository.deleteByBuyer_Id(buyerId);
    }
}