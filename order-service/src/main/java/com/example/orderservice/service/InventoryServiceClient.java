package com.example.orderservice.service;

import com.example.orderservice.dto.ReserveInventoryRequest;
import com.example.orderservice.dto.ReserveInventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", url = "${inventory.service.url:http://localhost:8081}")
public interface InventoryServiceClient {

    @PostMapping("/inventory/reserve")
    ReserveInventoryResponse reserveInventory(@RequestBody ReserveInventoryRequest request);
}
