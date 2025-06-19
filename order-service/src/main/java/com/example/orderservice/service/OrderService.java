package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.ReserveInventoryRequest;
import com.example.orderservice.dto.ReserveInventoryResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final InventoryServiceClient inventoryServiceClient;
    // private final PromotionServiceClient promotionServiceClient; // Commented out
    // - not implemented yet

    public OrderService(OrderRepository orderRepository,
            InventoryServiceClient inventoryServiceClient) {
        this.orderRepository = orderRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        // this.promotionServiceClient = promotionServiceClient; // Commented out - not
        // implemented yet
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}, item: {}, quantity: {}",
                request.getUserId(), request.getItemId(), request.getQuantity());

        // Reserve inventory
        ReserveInventoryRequest inventoryRequest = new ReserveInventoryRequest();
        inventoryRequest.setItemId(request.getItemId().toString());
        inventoryRequest.setQuantity(request.getQuantity());

        ReserveInventoryResponse inventoryResponse = inventoryServiceClient.reserveInventory(inventoryRequest);

        if (!inventoryResponse.isSuccess()) {
            throw new RuntimeException("Failed to reserve inventory for item: " + request.getItemId() +
                    " - " + inventoryResponse.getMessage());
        }

        // Calculate total price (for now, using a simple calculation)
        // In a real application, you would get the price from the product catalog
        // service
        BigDecimal unitPrice = BigDecimal.valueOf(10.00); // Placeholder price
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        // Apply promotion if available - commented out since promotion service is not
        // implemented yet
        // BigDecimal discount = promotionServiceClient.applyPromotion(
        // request.getUserId(), request.getItemId(), request.getQuantity());
        // totalPrice = totalPrice.subtract(discount);

        // Create and save the order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setItemId(request.getItemId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getOrderId());

        return mapToOrderResponse(savedOrder);
    }

    public Optional<OrderResponse> getOrder(Long orderId) {
        log.info("Retrieving order with ID: {}", orderId);
        return orderRepository.findById(orderId)
                .map(this::mapToOrderResponse);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        log.info("Retrieving orders for user: {}", userId);
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUserId());
        response.setItemId(order.getItemId());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setCreatedAt(order.getCreatedAt());
        response.setStatus("CREATED");
        return response;
    }
}
