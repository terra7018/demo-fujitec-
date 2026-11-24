package com.example.stock.item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "item")
public class Item {

    @Id
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "min_stock", nullable = false)
    private BigDecimal minStock;

    @Column(name = "max_stock")
    private BigDecimal maxStock;

    protected Item() {
    }

    public Item(String code, String description, String unit, BigDecimal quantity, BigDecimal minStock, BigDecimal maxStock) {
        this.code = code;
        this.description = description;
        this.unit = unit;
        this.quantity = quantity;
        this.minStock = minStock;
        this.maxStock = maxStock;
    }

    public void receive(BigDecimal amount) {
        quantity = quantity.add(amount);
    }

    public void issue(BigDecimal amount) {
        if (quantity.compareTo(amount) < 0) {
            throw new InsufficientStockException(code, quantity, amount);
        }
        quantity = quantity.subtract(amount);
    }

    public boolean isBelowMinimum() {
        return quantity.compareTo(minStock) < 0;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
    public String getUnit() { return unit; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getMinStock() { return minStock; }
    public BigDecimal getMaxStock() { return maxStock; }
}
