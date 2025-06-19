package com.example.orderservice.dto;

public class ReserveInventoryRequest {
    private String itemId;
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
}
