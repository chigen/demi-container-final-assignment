package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.ReserveInventoryRequest;
import com.example.orderservice.dto.ReserveInventoryResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InventoryServiceClient inventoryServiceClient;
    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, inventoryServiceClient);
    }

    @Test
    void createOrder_success() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 2L, 3);
        ReserveInventoryResponse inventoryResponse = new ReserveInventoryResponse();
        inventoryResponse.setSuccess(true);
        when(inventoryServiceClient.reserveInventory(any(ReserveInventoryRequest.class))).thenReturn(inventoryResponse);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setOrderId(100L);
            return o;
        });

        var response = orderService.createOrder(request);
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(2L, response.getItemId());
        assertEquals(3, response.getQuantity());
        assertEquals(BigDecimal.valueOf(30.00), response.getTotalPrice());
        assertEquals(100L, response.getOrderId());
        assertEquals("CREATED", response.getStatus());
    }

    @Test
    void createOrder_inventoryFailure() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 2L, 3);
        ReserveInventoryResponse inventoryResponse = new ReserveInventoryResponse();
        inventoryResponse.setSuccess(false);
        inventoryResponse.setMessage("Out of stock");
        when(inventoryServiceClient.reserveInventory(any(ReserveInventoryRequest.class))).thenReturn(inventoryResponse);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.createOrder(request));
        assertTrue(ex.getMessage().contains("Failed to reserve inventory"));
    }
}
