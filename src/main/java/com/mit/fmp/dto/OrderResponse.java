package com.mit.fmp.dto;

import java.time.LocalDateTime;

public class OrderResponse {

	private Long id;
	private Long buyerId;
	private String buyerName;
	private Long productId;
	private String productName;
	private Long farmerId;
	private String farmerName;
	private Integer quantity;
	private Double totalPrice;
	private String status;
	private LocalDateTime orderDate;

	public OrderResponse(Long id, Long buyerId, String buyerName, Long productId, String productName, Long farmerId,
			String farmerName, Integer quantity, Double totalPrice, String status, LocalDateTime orderDate) {

		this.id = id;
		this.buyerId = buyerId;
		this.buyerName = buyerName;
		this.productId = productId;
		this.productName = productName;
		this.farmerId = farmerId;
		this.farmerName = farmerName;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.status = status;
		this.orderDate = orderDate;
	}

	public Long getId() {
		return id;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public Long getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}

	public Long getFarmerId() {
		return farmerId;
	}

	public String getFarmerName() {
		return farmerName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public String getStatus() {
		return status;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}
}
