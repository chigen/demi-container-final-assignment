package com.example.inventoryservice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void testInventoryResponse_ShouldWorkCorrectly() {
        // When
        InventoryResponse response = new InventoryResponse("ITEM001", 100, 10, 90);

        // Then
        assertThat(response.getItemId()).isEqualTo("ITEM001");
        assertThat(response.getStock()).isEqualTo(100);
        assertThat(response.getReservedStock()).isEqualTo(10);
        assertThat(response.getAvailableStock()).isEqualTo(90);

        // Test setters
        response.setItemId("ITEM002");
        response.setStock(200);
        response.setReservedStock(20);
        response.setAvailableStock(180);

        assertThat(response.getItemId()).isEqualTo("ITEM002");
        assertThat(response.getStock()).isEqualTo(200);
        assertThat(response.getReservedStock()).isEqualTo(20);
        assertThat(response.getAvailableStock()).isEqualTo(180);
    }

    @Test
    void testReserveInventoryRequest_ShouldWorkCorrectly() {
        // When
        ReserveInventoryRequest request = new ReserveInventoryRequest("ITEM001", 5);

        // Then
        assertThat(request.getItemId()).isEqualTo("ITEM001");
        assertThat(request.getQuantity()).isEqualTo(5);

        // Test setters
        request.setItemId("ITEM002");
        request.setQuantity(10);

        assertThat(request.getItemId()).isEqualTo("ITEM002");
        assertThat(request.getQuantity()).isEqualTo(10);
    }

    @Test
    void testReserveInventoryResponse_ShouldWorkCorrectly() {
        // When
        ReserveInventoryResponse response = new ReserveInventoryResponse(
                true, "Success", "ITEM001", 5, 85);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Success");
        assertThat(response.getItemId()).isEqualTo("ITEM001");
        assertThat(response.getReservedQuantity()).isEqualTo(5);
        assertThat(response.getRemainingAvailableStock()).isEqualTo(85);

        // Test setters
        response.setSuccess(false);
        response.setMessage("Failed");
        response.setItemId("ITEM002");
        response.setReservedQuantity(0);
        response.setRemainingAvailableStock(0);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Failed");
        assertThat(response.getItemId()).isEqualTo("ITEM002");
        assertThat(response.getReservedQuantity()).isEqualTo(0);
        assertThat(response.getRemainingAvailableStock()).isEqualTo(0);
    }

    @Test
    void testNoArgsConstructor_ShouldCreateEmptyObjects() {
        // When
        InventoryResponse inventoryResponse = new InventoryResponse();
        ReserveInventoryRequest reserveRequest = new ReserveInventoryRequest();
        ReserveInventoryResponse reserveResponse = new ReserveInventoryResponse();

        // Then
        assertThat(inventoryResponse.getItemId()).isNull();
        assertThat(inventoryResponse.getStock()).isNull();
        assertThat(inventoryResponse.getReservedStock()).isNull();
        assertThat(inventoryResponse.getAvailableStock()).isNull();

        assertThat(reserveRequest.getItemId()).isNull();
        assertThat(reserveRequest.getQuantity()).isNull();

        assertThat(reserveResponse.isSuccess()).isFalse();
        assertThat(reserveResponse.getMessage()).isNull();
        assertThat(reserveResponse.getItemId()).isNull();
        assertThat(reserveResponse.getReservedQuantity()).isNull();
        assertThat(reserveResponse.getRemainingAvailableStock()).isNull();
    }

    @Test
    void testEqualsAndHashCode_ShouldWorkCorrectly() {
        // Given
        InventoryResponse response1 = new InventoryResponse("ITEM001", 100, 10, 90);
        InventoryResponse response2 = new InventoryResponse("ITEM001", 100, 10, 90);
        InventoryResponse response3 = new InventoryResponse("ITEM002", 100, 10, 90);

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1).isNotEqualTo(response3);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString_ShouldContainAllFields() {
        // When
        InventoryResponse response = new InventoryResponse("ITEM001", 100, 10, 90);
        ReserveInventoryRequest request = new ReserveInventoryRequest("ITEM001", 5);
        ReserveInventoryResponse reserveResponse = new ReserveInventoryResponse(true, "Success", "ITEM001", 5, 85);

        // Then
        assertThat(response.toString()).contains("ITEM001");
        assertThat(response.toString()).contains("100");
        assertThat(response.toString()).contains("10");
        assertThat(response.toString()).contains("90");

        assertThat(request.toString()).contains("ITEM001");
        assertThat(request.toString()).contains("5");

        assertThat(reserveResponse.toString()).contains("ITEM001");
        assertThat(reserveResponse.toString()).contains("Success");
        assertThat(reserveResponse.toString()).contains("5");
        assertThat(reserveResponse.toString()).contains("85");
    }
}
