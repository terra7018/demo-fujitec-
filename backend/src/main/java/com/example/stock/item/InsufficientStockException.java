package com.example.stock.item;

import java.math.BigDecimal;

public class InsufficientStockException extends RuntimeException {
    private final BigDecimal available;

    public InsufficientStockException(String code, BigDecimal available, BigDecimal requested) {
        super("在庫が不足しています（品目 " + code + "：在庫 " + available.stripTrailingZeros().toPlainString()
                + "、出庫 " + requested.stripTrailingZeros().toPlainString() + "）");
        this.available = available;
    }

    public BigDecimal getAvailable() { return available; }
}
