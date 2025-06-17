package com.example.inventoryservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false, unique = true)
    private String itemId;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "reserved_stock", nullable = false)
    private Integer reservedStock = 0;

    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;

    // Constructors
    public Inventory() {
    }

    public Inventory(Long id, String itemId, Integer stock, Integer reservedStock, Integer availableStock) {
        this.id = id;
        this.itemId = itemId;
        this.stock = stock;
        this.reservedStock = reservedStock;
        this.availableStock = availableStock;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @PrePersist
    @PreUpdate
    private void calculateAvailableStock() {
        this.availableStock = this.stock - this.reservedStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Inventory inventory = (Inventory) o;

        if (id != null ? !id.equals(inventory.id) : inventory.id != null)
            return false;
        if (itemId != null ? !itemId.equals(inventory.itemId) : inventory.itemId != null)
            return false;
        if (stock != null ? !stock.equals(inventory.stock) : inventory.stock != null)
            return false;
        if (reservedStock != null ? !reservedStock.equals(inventory.reservedStock) : inventory.reservedStock != null)
            return false;
        return availableStock != null ? availableStock.equals(inventory.availableStock)
                : inventory.availableStock == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
        result = 31 * result + (stock != null ? stock.hashCode() : 0);
        result = 31 * result + (reservedStock != null ? reservedStock.hashCode() : 0);
        result = 31 * result + (availableStock != null ? availableStock.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", itemId='" + itemId + '\'' +
                ", stock=" + stock +
                ", reservedStock=" + reservedStock +
                ", availableStock=" + availableStock +
                '}';
    }
}
