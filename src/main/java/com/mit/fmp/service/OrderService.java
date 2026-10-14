package com.mit.fmp.service;


import java.util.List;

import com.mit.fmp.dto.OrderRequest;
import com.mit.fmp.dto.OrderResponse;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest request);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getOrdersByBuyer(Long buyerId);

    List<OrderResponse> getOrdersByFarmer(Long farmerId);

    OrderResponse updateOrderStatus(Long id, String status);
}
