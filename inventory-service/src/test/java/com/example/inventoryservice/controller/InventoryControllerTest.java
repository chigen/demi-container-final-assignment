package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.ReserveInventoryRequest;
import com.example.inventoryservice.dto.ReserveInventoryResponse;
import com.example.inventoryservice.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private InventoryService inventoryService;

        @Autowired
        private ObjectMapper objectMapper;

        private InventoryResponse mockInventoryResponse;
        private ReserveInventoryRequest mockReserveRequest;
        private ReserveInventoryResponse mockReserveResponse;

        @BeforeEach
        void setUp() {
                // set mock inventory response
                mockInventoryResponse = new InventoryResponse(
                                "ITEM001",
                                100,
                                10,
                                90);

                // set mock reserve request
                mockReserveRequest = new ReserveInventoryRequest(
                                "ITEM001",
                                5);

                // set mock reserve response
                mockReserveResponse = new ReserveInventoryResponse(
                                true,
                                "Inventory reserved successfully",
                                "ITEM001",
                                5,
                                85);
        }

        @Test
        void getInventory_WhenItemExists_ShouldReturnInventory() throws Exception {
                // Given
                when(inventoryService.getInventory("ITEM001"))
                                .thenReturn(Optional.of(mockInventoryResponse));

                // When & Then
                mockMvc.perform(get("/inventory/ITEM001"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.itemId").value("ITEM001"))
                                .andExpect(jsonPath("$.stock").value(100))
                                .andExpect(jsonPath("$.reservedStock").value(10))
                                .andExpect(jsonPath("$.availableStock").value(90));
        }

        @Test
        void getInventory_WhenItemNotExists_ShouldReturnNotFound() throws Exception {
                // Given
                when(inventoryService.getInventory("NONEXISTENT"))
                                .thenReturn(Optional.empty());

                // When & Then
                mockMvc.perform(get("/inventory/NONEXISTENT"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void reserveInventory_WhenValidRequest_ShouldReturnSuccess() throws Exception {
                // Given
                when(inventoryService.reserveInventory(any(ReserveInventoryRequest.class)))
                                .thenReturn(mockReserveResponse);

                // When & Then
                mockMvc.perform(post("/inventory/reserve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockReserveRequest)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.message").value("Inventory reserved successfully"))
                                .andExpect(jsonPath("$.itemId").value("ITEM001"))
                                .andExpect(jsonPath("$.reservedQuantity").value(5))
                                .andExpect(jsonPath("$.remainingAvailableStock").value(85));
        }

        @Test
        void reserveInventory_WhenItemNotExists_ShouldReturnBadRequest() throws Exception {
                // Given
                ReserveInventoryResponse failureResponse = new ReserveInventoryResponse(
                                false,
                                "Item not found",
                                "NONEXISTENT",
                                0,
                                0);
                when(inventoryService.reserveInventory(any(ReserveInventoryRequest.class)))
                                .thenReturn(failureResponse);

                ReserveInventoryRequest request = new ReserveInventoryRequest("NONEXISTENT", 5);

                // When & Then
                mockMvc.perform(post("/inventory/reserve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Item not found"));
        }

        @Test
        void reserveInventory_WhenInsufficientStock_ShouldReturnBadRequest() throws Exception {
                // Given
                ReserveInventoryResponse failureResponse = new ReserveInventoryResponse(
                                false,
                                "Insufficient stock. Available: 3",
                                "ITEM001",
                                0,
                                3);
                when(inventoryService.reserveInventory(any(ReserveInventoryRequest.class)))
                                .thenReturn(failureResponse);

                ReserveInventoryRequest request = new ReserveInventoryRequest("ITEM001", 10);

                // When & Then
                mockMvc.perform(post("/inventory/reserve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Insufficient stock. Available: 3"));
        }

        @Test
        void reserveInventory_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
                // Given - invalid request (quantity is 0)
                ReserveInventoryRequest invalidRequest = new ReserveInventoryRequest("ITEM001", 0);

                // When & Then
                mockMvc.perform(post("/inventory/reserve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void reserveInventory_WhenEmptyItemId_ShouldReturnBadRequest() throws Exception {
                // Given - empty item id
                ReserveInventoryRequest invalidRequest = new ReserveInventoryRequest("", 5);

                // When & Then
                mockMvc.perform(post("/inventory/reserve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }
}
