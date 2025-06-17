package com.example.inventoryservice.dto;

public class InventoryResponse {
    private String itemId;
    private Integer stock;
    private Integer reservedStock;
    private Integer availableStock;

    // Constructors
    public InventoryResponse() {
    }

    public InventoryResponse(String itemId, Integer stock, Integer reservedStock, Integer availableStock) {
        this.itemId = itemId;
        this.stock = stock;
        this.reservedStock = reservedStock;
        this.availableStock = availableStock;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock) {
        this.reservedStock = reservedStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        InventoryResponse that = (InventoryResponse) o;

        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null)
            return false;
        if (stock != null ? !stock.equals(that.stock) : that.stock != null)
            return false;
        if (reservedStock != null ? !reservedStock.equals(that.reservedStock) : that.reservedStock != null)
            return false;
        return availableStock != null ? availableStock.equals(that.availableStock) : that.availableStock == null;
    }

    @Override
    public int hashCode() {
        int result = itemId != null ? itemId.hashCode() : 0;
        result = 31 * result + (stock != null ? stock.hashCode() : 0);
        result = 31 * result + (reservedStock != null ? reservedStock.hashCode() : 0);
        result = 31 * result + (availableStock != null ? availableStock.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InventoryResponse{" +
                "itemId='" + itemId + '\'' +
                ", stock=" + stock +
                ", reservedStock=" + reservedStock +
                ", availableStock=" + availableStock +
                '}';
    }
}
