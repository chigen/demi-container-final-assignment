package com.example.orderservice.dto;

public class ReserveInventoryResponse {
    private boolean success;
    private String message;
    private String itemId;
    private Integer reservedQuantity;
    private Integer remainingAvailableStock;

    // Constructors
    public ReserveInventoryResponse() {
    }

    public ReserveInventoryResponse(boolean success, String message, String itemId,
            Integer reservedQuantity, Integer remainingAvailableStock) {
        this.success = success;
        this.message = message;
        this.itemId = itemId;
        this.reservedQuantity = reservedQuantity;
        this.remainingAvailableStock = remainingAvailableStock;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Integer getRemainingAvailableStock() {
        return remainingAvailableStock;
    }

    public void setRemainingAvailableStock(Integer remainingAvailableStock) {
        this.remainingAvailableStock = remainingAvailableStock;
    }
}
