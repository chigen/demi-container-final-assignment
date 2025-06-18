package com.example.inventoryservice;

import com.example.inventoryservice.dto.ReserveInventoryRequest;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class IntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        inventoryRepository.deleteAll();

        Inventory testInventory = new Inventory();
        testInventory.setItemId("TEST001");
        testInventory.setStock(100);
        testInventory.setReservedStock(10);
        testInventory.setAvailableStock(90);
        inventoryRepository.save(testInventory);
    }

    @Test
    void testGetInventory_ShouldReturnInventoryData() throws Exception {
        // When & Then
        mockMvc.perform(get("/inventory/TEST001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemId").value("TEST001"))
                .andExpect(jsonPath("$.stock").value(100))
                .andExpect(jsonPath("$.reservedStock").value(10))
                .andExpect(jsonPath("$.availableStock").value(90));
    }

    @Test
    void testGetInventory_WhenItemNotExists_ShouldReturnNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/inventory/NONEXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testReserveInventory_WhenValidRequest_ShouldReserveSuccessfully() throws Exception {
        // Given
        ReserveInventoryRequest request = new ReserveInventoryRequest("TEST001", 5);

        // When & Then
        mockMvc.perform(post("/inventory/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inventory reserved successfully"))
                .andExpect(jsonPath("$.itemId").value("TEST001"))
                .andExpect(jsonPath("$.reservedQuantity").value(5))
                .andExpect(jsonPath("$.remainingAvailableStock").value(85));

        Inventory updatedInventory = inventoryRepository.findByItemId("TEST001").orElse(null);
        assertThat(updatedInventory).isNotNull();
        assertThat(updatedInventory.getReservedStock()).isEqualTo(15); // 10 + 5
        assertThat(updatedInventory.getAvailableStock()).isEqualTo(85); // 100 - 15
    }

    @Test
    void testReserveInventory_WhenItemNotExists_ShouldReturnBadRequest() throws Exception {
        // Given
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
    void testReserveInventory_WhenInsufficientStock_ShouldReturnBadRequest() throws Exception {
        // Given
        ReserveInventoryRequest request = new ReserveInventoryRequest("TEST001", 100);

        // When & Then
        mockMvc.perform(post("/inventory/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Insufficient stock. Available: 90"));

        Inventory inventory = inventoryRepository.findByItemId("TEST001").orElse(null);
        assertThat(inventory).isNotNull();
        assertThat(inventory.getReservedStock()).isEqualTo(10);
        assertThat(inventory.getAvailableStock()).isEqualTo(90);
    }

    @Test
    void testReserveInventory_WhenExactAvailableStock_ShouldReserveSuccessfully() throws Exception {
        // Given
        ReserveInventoryRequest request = new ReserveInventoryRequest("TEST001", 90);

        // When & Then
        mockMvc.perform(post("/inventory/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.reservedQuantity").value(90))
                .andExpect(jsonPath("$.remainingAvailableStock").value(0));

        Inventory updatedInventory = inventoryRepository.findByItemId("TEST001").orElse(null);
        assertThat(updatedInventory).isNotNull();
        assertThat(updatedInventory.getReservedStock()).isEqualTo(100); // 10 + 90
        assertThat(updatedInventory.getAvailableStock()).isEqualTo(0); // 100 - 100
    }

    @Test
    void testReserveInventory_WhenInvalidQuantity_ShouldReturnBadRequest() throws Exception {
        // Given - 数量为0
        ReserveInventoryRequest request = new ReserveInventoryRequest("TEST001", 0);

        // When & Then
        mockMvc.perform(post("/inventory/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testReserveInventory_WhenEmptyItemId_ShouldReturnBadRequest() throws Exception {
        // Given - 空的商品ID
        ReserveInventoryRequest request = new ReserveInventoryRequest("", 5);

        // When & Then
        mockMvc.perform(post("/inventory/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMultipleReservations_ShouldWorkCorrectly() throws Exception {
        // Given
        ReserveInventoryRequest request1 = new ReserveInventoryRequest("TEST001", 20);
        ReserveInventoryRequest request2 = new ReserveInventoryRequest("TEST001", 30);

        // When & Then
        mockMvc.perform(post("/inventory/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.reservedQuantity").value(20))
                .andExpect(jsonPath("$.remainingAvailableStock").value(70));

        mockMvc.perform(post("/inventory/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.reservedQuantity").value(30))
                .andExpect(jsonPath("$.remainingAvailableStock").value(40));

        Inventory finalInventory = inventoryRepository.findByItemId("TEST001").orElse(null);
        assertThat(finalInventory).isNotNull();
        assertThat(finalInventory.getReservedStock()).isEqualTo(60);
        assertThat(finalInventory.getAvailableStock()).isEqualTo(40); // 100 - 60
    }
}
