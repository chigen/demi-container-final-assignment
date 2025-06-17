package com.example.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReserveInventoryRequest {

    @NotBlank(message = "Item ID is required")
    private String itemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // Constructors
    public ReserveInventoryRequest() {
    }

    public ReserveInventoryRequest(String itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ReserveInventoryRequest that = (ReserveInventoryRequest) o;

        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null)
            return false;
        return quantity != null ? quantity.equals(that.quantity) : that.quantity == null;
    }

    @Override
    public int hashCode() {
        int result = itemId != null ? itemId.hashCode() : 0;
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReserveInventoryRequest{" +
                "itemId='" + itemId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
