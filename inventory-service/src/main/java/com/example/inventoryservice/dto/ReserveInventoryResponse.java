package com.example.inventoryservice.dto;

public class ReserveInventoryResponse {
    private boolean success;
    private String message;
    private String itemId;
    private Integer reservedQuantity;
    private Integer remainingAvailableStock;

    // Constructors
    public ReserveInventoryResponse() {
    }

    public ReserveInventoryResponse(boolean success, String message, String itemId, Integer reservedQuantity,
            Integer remainingAvailableStock) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ReserveInventoryResponse that = (ReserveInventoryResponse) o;

        if (success != that.success)
            return false;
        if (message != null ? !message.equals(that.message) : that.message != null)
            return false;
        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null)
            return false;
        if (reservedQuantity != null ? !reservedQuantity.equals(that.reservedQuantity) : that.reservedQuantity != null)
            return false;
        return remainingAvailableStock != null ? remainingAvailableStock.equals(that.remainingAvailableStock)
                : that.remainingAvailableStock == null;
    }

    @Override
    public int hashCode() {
        int result = (success ? 1 : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
        result = 31 * result + (reservedQuantity != null ? reservedQuantity.hashCode() : 0);
        result = 31 * result + (remainingAvailableStock != null ? remainingAvailableStock.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReserveInventoryResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", itemId='" + itemId + '\'' +
                ", reservedQuantity=" + reservedQuantity +
                ", remainingAvailableStock=" + remainingAvailableStock +
                '}';
    }
}
