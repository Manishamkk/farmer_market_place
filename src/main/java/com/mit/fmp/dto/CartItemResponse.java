package com.mit.fmp.dto;


import java.math.BigDecimal;

public class CartItemResponse {

    private Long cartItemId;
    private Long buyerId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

    public CartItemResponse(
            Long cartItemId,
            Long buyerId,
            Long productId,
            String productName,
            BigDecimal price,
            Integer quantity,
            BigDecimal subtotal) {

        this.cartItemId = cartItemId;
        this.buyerId = buyerId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}