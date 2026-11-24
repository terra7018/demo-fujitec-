package com.example.stock.item;

import java.math.BigDecimal;

public record ItemResponse(String code, String description, String unit, BigDecimal quantity,
                           BigDecimal minStock, BigDecimal maxStock, boolean belowMinimum) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getCode(), item.getDescription(), item.getUnit(), item.getQuantity(),
                item.getMinStock(), item.getMaxStock(), item.isBelowMinimum());
    }
}
