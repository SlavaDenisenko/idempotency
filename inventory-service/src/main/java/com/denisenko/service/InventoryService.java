package com.denisenko.service;

import com.denisenko.dto.StockDto;
import com.denisenko.event.OrderLineItem;
import com.denisenko.model.Stock;
import com.denisenko.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final StockRepository stockRepository;

    public List<StockDto> addStocks(List<StockDto> stocksDto) {
        List<Stock> stocks = new ArrayList<>();
        stocksDto.forEach(stockDto -> {
            Stock stock = mapToEntity(stockDto);
            stock.setReserved(0);
            stocks.add(stock);
        });
        return stockRepository.saveAll(stocks).stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<StockDto> getStocks() {
        return stockRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public boolean reserveItems(List<OrderLineItem> orderLineItems) {
        boolean isReserved = true;
        List<Stock> stocksToUpdate = new ArrayList<>();
        for (OrderLineItem orderLineItem : orderLineItems) {
            String itemId = orderLineItem.getItemId();
            Integer requestedQuantity = orderLineItem.getQuantity();

            Stock stock = stockRepository.findByItemId(itemId).orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
            if (stock.getQuantity() - stock.getReserved() < requestedQuantity) {
                isReserved = false;
                break;
            }

            stock.setReserved(stock.getReserved() + requestedQuantity);
            stocksToUpdate.add(stock);
        }

        if (isReserved)
            stockRepository.saveAll(stocksToUpdate);

        return isReserved;
    }

    public boolean releaseItems(List<OrderLineItem> orderLineItems) {
        boolean isReleased = true;
        List<Stock> stocksToUpdate = new ArrayList<>();
        for (OrderLineItem orderLineItem : orderLineItems) {
            String itemId = orderLineItem.getItemId();
            Integer requestedQuantity = orderLineItem.getQuantity();

            Stock stock = stockRepository.findByItemId(itemId).orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
            if (stock.getReserved() < requestedQuantity) {
                isReleased = false;
                break;
            }

            stock.setReserved(stock.getReserved() - requestedQuantity);
            stocksToUpdate.add(stock);
        }

        if (isReleased)
            stockRepository.saveAll(stocksToUpdate);

        return isReleased;
    }

    private Stock mapToEntity(StockDto stockDto) {
        return Stock.builder()
                .itemId(stockDto.getItemId())
                .quantity(stockDto.getQuantity())
                .build();
    }

    private StockDto mapToDto(Stock stock) {
        return StockDto.builder()
                .itemId(stock.getItemId())
                .quantity(stock.getQuantity())
                .reserved(stock.getReserved())
                .build();
    }
}
