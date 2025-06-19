package com.example.orderservice.controller;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_success() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(1L, "ITEM001", 3);
        OrderResponse response = new OrderResponse();
        response.setOrderId(10L);
        response.setUserId(1L);
        response.setItemId("ITEM001");
        response.setQuantity(3);
        response.setTotalPrice(BigDecimal.valueOf(30.00));
        response.setCreatedAt(LocalDateTime.now());
        response.setStatus("CREATED");
        Mockito.when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(10L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.itemId").value("ITEM001"))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void getOrder_success() throws Exception {
        OrderResponse response = new OrderResponse();
        response.setOrderId(10L);
        response.setUserId(1L);
        response.setItemId("ITEM001");
        response.setQuantity(3);
        response.setTotalPrice(BigDecimal.valueOf(30.00));
        response.setCreatedAt(LocalDateTime.now());
        response.setStatus("CREATED");
        Mockito.when(orderService.getOrder(10L)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/order/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(10L))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void getOrder_notFound() throws Exception {
        Mockito.when(orderService.getOrder(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/order/99"))
                .andExpect(status().isNotFound());
    }
}
