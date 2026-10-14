package com.mit.fmp.service;

import com.mit.fmp.dto.OrderRequest;
import com.mit.fmp.dto.OrderResponse;
import com.mit.fmp.entity.CustomerOrder;
import com.mit.fmp.entity.Product;
import com.mit.fmp.entity.Users;
import com.mit.fmp.exception.OrderNotFoundException;
import com.mit.fmp.exception.ProductNotFoundException;
import com.mit.fmp.exception.UserNotFoundException;
import com.mit.fmp.repository.OrderRepository;
import com.mit.fmp.repository.ProductRepository;
import com.mit.fmp.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // PLACE ORDER
    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {

        // Validate quantity
        if (request.getQuantity() == null ||
                request.getQuantity() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order quantity must be greater than zero");
        }

        // Find buyer
        Users buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Buyer not found with ID: "
                                + request.getBuyerId()));

        if (!"BUYER".equalsIgnoreCase(buyer.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only buyers can place orders");
        }

        // Find product
        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with ID: "
                                + request.getProductId()));

        // Check farmer relationship
        if (product.getFarmer() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product has no assigned farmer");
        }

        // Check available stock
        if (product.getQuantity() == null ||
                request.getQuantity().doubleValue()
                        > product.getQuantity()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient product quantity");
        }

        // Validate product price
        if (product.getPrice() == null ||
                product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product price must be greater than zero");
        }

        // Calculate total price using BigDecimal
        BigDecimal total = product.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        double totalPrice = total.doubleValue();

        // Create order
        CustomerOrder order = new CustomerOrder();
        order.setBuyer(buyer);
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setStatus("PLACED");

        // Reduce product stock
        product.setQuantity(
                product.getQuantity()
                        - request.getQuantity().doubleValue());

        productRepository.save(product);

        // Save order
        CustomerOrder savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    // GET ALL ORDERS
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET ORDER BY ID
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {

        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with ID: " + id));

        return convertToResponse(order);
    }

    // GET ORDERS BY BUYER
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByBuyer(Long buyerId) {

        Users buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Buyer not found with ID: " + buyerId));

        if (!"BUYER".equalsIgnoreCase(buyer.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is not a buyer");
        }

        return orderRepository.findByBuyer_Id(buyerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET ORDERS BY FARMER
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByFarmer(Long farmerId) {

        Users farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Farmer not found with ID: " + farmerId));

        if (!"FARMER".equalsIgnoreCase(farmer.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is not a farmer");
        }

        return orderRepository.findByProduct_Farmer_Id(farmerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // UPDATE ORDER STATUS
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status) {

        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with ID: " + id));

        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required");
        }

        String newStatus = status.trim().toUpperCase(Locale.ROOT);

        if (!List.of(
                "CONFIRMED",
                "SHIPPED",
                "DELIVERED",
                "CANCELLED"
        ).contains(newStatus)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid order status");
        }

        if ("CANCELLED".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cancelled order cannot be updated");
        }

        if ("DELIVERED".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Delivered order cannot be updated");
        }

        // Restore stock when an order is cancelled
        if ("CANCELLED".equals(newStatus)) {

            if ("SHIPPED".equals(order.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Shipped order cannot be cancelled");
            }

            Product product = order.getProduct();

            product.setQuantity(
                    product.getQuantity()
                            + order.getQuantity().doubleValue());

            productRepository.save(product);
        }

        order.setStatus(newStatus);

        CustomerOrder updatedOrder = orderRepository.save(order);

        return convertToResponse(updatedOrder);
    }

    // ENTITY TO RESPONSE DTO
    private OrderResponse convertToResponse(CustomerOrder order) {

        Product product = order.getProduct();
        Users buyer = order.getBuyer();
        Users farmer = product.getFarmer();

        return new OrderResponse(
                order.getId(),
                buyer.getId(),
                buyer.getName(),
                product.getId(),
                product.getName(),
                farmer.getId(),
                farmer.getName(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getOrderDate()
        );
    }
}