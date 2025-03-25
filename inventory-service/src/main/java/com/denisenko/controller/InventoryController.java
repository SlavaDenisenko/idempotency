package com.denisenko.controller;

import com.denisenko.dto.StockDto;
import com.denisenko.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory/stocks")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<List<StockDto>> addStocks(@RequestBody List<StockDto> stocksDto) {
        return ResponseEntity.ok(inventoryService.addStocks(stocksDto));
    }

    @GetMapping
    public ResponseEntity<List<StockDto>> getStocks() {
        return ResponseEntity.ok(inventoryService.getStocks());
    }
}
