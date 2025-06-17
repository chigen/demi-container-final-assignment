package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.ReserveInventoryRequest;
import com.example.inventoryservice.dto.ReserveInventoryResponse;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Optional<InventoryResponse> getInventory(String itemId) {
        log.info("Querying inventory for item: {}", itemId);

        return inventoryRepository.findByItemId(itemId)
                .map(inventory -> new InventoryResponse(
                        inventory.getItemId(),
                        inventory.getStock(),
                        inventory.getReservedStock(),
                        inventory.getAvailableStock()));
    }

    @Transactional
    public ReserveInventoryResponse reserveInventory(ReserveInventoryRequest request) {
        log.info("Reserving inventory for item: {}, quantity: {}", request.getItemId(), request.getQuantity());

        Optional<Inventory> inventoryOpt = inventoryRepository.findByItemId(request.getItemId());

        if (inventoryOpt.isEmpty()) {
            log.warn("Item not found: {}", request.getItemId());
            return new ReserveInventoryResponse(false, "Item not found", request.getItemId(), 0, 0);
        }

        Inventory inventory = inventoryOpt.get();

        if (inventory.getAvailableStock() < request.getQuantity()) {
            log.warn("Insufficient stock for item: {}. Available: {}, Requested: {}",
                    request.getItemId(), inventory.getAvailableStock(), request.getQuantity());
            return new ReserveInventoryResponse(
                    false,
                    "Insufficient stock. Available: " + inventory.getAvailableStock(),
                    request.getItemId(),
                    0,
                    inventory.getAvailableStock());
        }

        // Reserve the inventory
        inventory.setReservedStock(inventory.getReservedStock() + request.getQuantity());
        // Recalculate available stock
        inventory.setAvailableStock(inventory.getStock() - inventory.getReservedStock());
        inventoryRepository.save(inventory);

        log.info("Successfully reserved {} units for item: {}", request.getQuantity(), request.getItemId());

        return new ReserveInventoryResponse(
                true,
                "Inventory reserved successfully",
                request.getItemId(),
                request.getQuantity(),
                inventory.getAvailableStock());
    }
}
