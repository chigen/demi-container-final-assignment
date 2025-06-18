package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.dto.ReserveInventoryRequest;
import com.example.inventoryservice.dto.ReserveInventoryResponse;
import com.example.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable String itemId) {
        log.info("GET /inventory/{} - Querying inventory", itemId);

        return inventoryService.getInventory(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReserveInventoryResponse> reserveInventory(
            @Valid @RequestBody ReserveInventoryRequest request) {
        log.info("POST /inventory/reserve - Reserving inventory for item: {}, quantity: {}",
                request.getItemId(), request.getQuantity());

        ReserveInventoryResponse response = inventoryService.reserveInventory(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
