package com.example.inventoryservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setItemId("ITEM001");
        inventory.setStock(100);
        inventory.setReservedStock(10);
    }

    @Test
    void testAvailableStockCalculation_ShouldCalculateCorrectly() {
        // Given
        inventory.setStock(100);
        inventory.setReservedStock(30);

        // When
        inventory.setAvailableStock(inventory.getStock() - inventory.getReservedStock());

        // Then
        assertThat(inventory.getAvailableStock()).isEqualTo(70);
    }

    @Test
    void testAvailableStockCalculation_WhenNoReservedStock_ShouldReturnFullStock() {
        // Given
        inventory.setStock(100);
        inventory.setReservedStock(0);

        // When
        inventory.setAvailableStock(inventory.getStock() - inventory.getReservedStock());

        // Then
        assertThat(inventory.getAvailableStock()).isEqualTo(100);
    }

    @Test
    void testAvailableStockCalculation_WhenAllStockReserved_ShouldReturnZero() {
        // Given
        inventory.setStock(100);
        inventory.setReservedStock(100);

        // When
        inventory.setAvailableStock(inventory.getStock() - inventory.getReservedStock());

        // Then
        assertThat(inventory.getAvailableStock()).isEqualTo(0);
    }

    @Test
    void testAllArgsConstructor_ShouldCreateInventoryCorrectly() {
        // When
        Inventory newInventory = new Inventory(2L, "ITEM002", 50, 5, 45);

        // Then
        assertThat(newInventory.getId()).isEqualTo(2L);
        assertThat(newInventory.getItemId()).isEqualTo("ITEM002");
        assertThat(newInventory.getStock()).isEqualTo(50);
        assertThat(newInventory.getReservedStock()).isEqualTo(5);
        assertThat(newInventory.getAvailableStock()).isEqualTo(45);
    }

    @Test
    void testNoArgsConstructor_ShouldCreateEmptyInventory() {
        // When
        Inventory emptyInventory = new Inventory();

        // Then
        assertThat(emptyInventory.getId()).isNull();
        assertThat(emptyInventory.getItemId()).isNull();
        assertThat(emptyInventory.getStock()).isNull();
        assertThat(emptyInventory.getReservedStock()).isEqualTo(0); // 默认值
        assertThat(emptyInventory.getAvailableStock()).isNull();
    }

    @Test
    void testSettersAndGetters_ShouldWorkCorrectly() {
        // When
        inventory.setId(3L);
        inventory.setItemId("ITEM003");
        inventory.setStock(200);
        inventory.setReservedStock(25);
        inventory.setAvailableStock(175);

        // Then
        assertThat(inventory.getId()).isEqualTo(3L);
        assertThat(inventory.getItemId()).isEqualTo("ITEM003");
        assertThat(inventory.getStock()).isEqualTo(200);
        assertThat(inventory.getReservedStock()).isEqualTo(25);
        assertThat(inventory.getAvailableStock()).isEqualTo(175);
    }

    @Test
    void testEqualsAndHashCode_ShouldWorkCorrectly() {
        // Given
        Inventory inventory1 = new Inventory(1L, "ITEM001", 100, 10, 90);
        Inventory inventory2 = new Inventory(1L, "ITEM001", 100, 10, 90);
        Inventory inventory3 = new Inventory(2L, "ITEM002", 100, 10, 90);

        // Then
        assertThat(inventory1).isEqualTo(inventory2);
        assertThat(inventory1).isNotEqualTo(inventory3);
        assertThat(inventory1.hashCode()).isEqualTo(inventory2.hashCode());
    }

    @Test
    void testToString_ShouldContainAllFields() {
        // When
        String toString = inventory.toString();

        // Then
        assertThat(toString).contains("ITEM001");
        assertThat(toString).contains("100");
        assertThat(toString).contains("10");
    }
}
