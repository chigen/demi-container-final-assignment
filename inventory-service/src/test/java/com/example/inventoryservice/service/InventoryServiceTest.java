package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.ReserveInventoryRequest;
import com.example.inventoryservice.dto.ReserveInventoryResponse;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory mockInventory;
    private ReserveInventoryRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockInventory = new Inventory();
        mockInventory.setId(1L);
        mockInventory.setItemId("ITEM001");
        mockInventory.setStock(100);
        mockInventory.setReservedStock(10);
        mockInventory.setAvailableStock(90);

        mockRequest = new ReserveInventoryRequest("ITEM001", 5);
    }

    @Test
    void getInventory_WhenItemExists_ShouldReturnInventoryResponse() {
        // Given
        when(inventoryRepository.findByItemId("ITEM001"))
                .thenReturn(Optional.of(mockInventory));

        // When
        Optional<InventoryResponse> result = inventoryService.getInventory("ITEM001");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getItemId()).isEqualTo("ITEM001");
        assertThat(result.get().getStock()).isEqualTo(100);
        assertThat(result.get().getReservedStock()).isEqualTo(10);
        assertThat(result.get().getAvailableStock()).isEqualTo(90);
    }

    @Test
    void getInventory_WhenItemNotExists_ShouldReturnEmpty() {
        // Given
        when(inventoryRepository.findByItemId("NONEXISTENT"))
                .thenReturn(Optional.empty());

        // When
        Optional<InventoryResponse> result = inventoryService.getInventory("NONEXISTENT");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void reserveInventory_WhenValidRequest_ShouldReserveSuccessfully() {
        // Given
        when(inventoryRepository.findByItemId("ITEM001"))
                .thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(mockInventory);

        // When
        ReserveInventoryResponse result = inventoryService.reserveInventory(mockRequest);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("Inventory reserved successfully");
        assertThat(result.getItemId()).isEqualTo("ITEM001");
        assertThat(result.getReservedQuantity()).isEqualTo(5);
        assertThat(result.getRemainingAvailableStock()).isEqualTo(85);

        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void reserveInventory_WhenItemNotExists_ShouldReturnFailure() {
        // Given
        when(inventoryRepository.findByItemId("NONEXISTENT"))
                .thenReturn(Optional.empty());

        ReserveInventoryRequest request = new ReserveInventoryRequest("NONEXISTENT", 5);

        // When
        ReserveInventoryResponse result = inventoryService.reserveInventory(request);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("Item not found");
        assertThat(result.getItemId()).isEqualTo("NONEXISTENT");
        assertThat(result.getReservedQuantity()).isEqualTo(0);
        assertThat(result.getRemainingAvailableStock()).isEqualTo(0);

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void reserveInventory_WhenInsufficientStock_ShouldReturnFailure() {
        // Given
        Inventory lowStockInventory = new Inventory();
        lowStockInventory.setItemId("ITEM001");
        lowStockInventory.setStock(100);
        lowStockInventory.setReservedStock(95);
        lowStockInventory.setAvailableStock(5);

        when(inventoryRepository.findByItemId("ITEM001"))
                .thenReturn(Optional.of(lowStockInventory));

        ReserveInventoryRequest request = new ReserveInventoryRequest("ITEM001", 10);

        // When
        ReserveInventoryResponse result = inventoryService.reserveInventory(request);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("Insufficient stock. Available: 5");
        assertThat(result.getItemId()).isEqualTo("ITEM001");
        assertThat(result.getReservedQuantity()).isEqualTo(0);
        assertThat(result.getRemainingAvailableStock()).isEqualTo(5);

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void reserveInventory_WhenExactAvailableStock_ShouldReserveSuccessfully() {
        // Given
        Inventory exactStockInventory = new Inventory();
        exactStockInventory.setItemId("ITEM001");
        exactStockInventory.setStock(100);
        exactStockInventory.setReservedStock(90);
        exactStockInventory.setAvailableStock(10);

        when(inventoryRepository.findByItemId("ITEM001"))
                .thenReturn(Optional.of(exactStockInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(exactStockInventory);

        ReserveInventoryRequest request = new ReserveInventoryRequest("ITEM001", 10);

        // When
        ReserveInventoryResponse result = inventoryService.reserveInventory(request);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("Inventory reserved successfully");
        assertThat(result.getItemId()).isEqualTo("ITEM001");
        assertThat(result.getReservedQuantity()).isEqualTo(10);
        assertThat(result.getRemainingAvailableStock()).isEqualTo(0);

        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void reserveInventory_WhenZeroQuantity_ShouldReserveSuccessfully() {
        // Given
        when(inventoryRepository.findByItemId("ITEM001"))
                .thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(mockInventory);

        ReserveInventoryRequest request = new ReserveInventoryRequest("ITEM001", 0);

        // When
        ReserveInventoryResponse result = inventoryService.reserveInventory(request);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("Inventory reserved successfully");
        assertThat(result.getItemId()).isEqualTo("ITEM001");
        assertThat(result.getReservedQuantity()).isEqualTo(0);
        assertThat(result.getRemainingAvailableStock()).isEqualTo(90);

        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void reserveInventory_ShouldUpdateReservedStockCorrectly() {
        // Given
        when(inventoryRepository.findByItemId("ITEM001"))
                .thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> {
                    Inventory savedInventory = invocation.getArgument(0);
                    assertThat(savedInventory.getReservedStock()).isEqualTo(15);
                    assertThat(savedInventory.getAvailableStock()).isEqualTo(85);
                    return savedInventory;
                });

        // When
        inventoryService.reserveInventory(mockRequest);

        // Then
        verify(inventoryRepository).save(any(Inventory.class));
    }
}
