package com.example.stock.movement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "stock_movement")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code", nullable = false)
    private String itemCode;

    @Column(nullable = false, columnDefinition = "bpchar(1)")
    private MovementType type;

    @Column(nullable = false)
    private BigDecimal quantity;

    private String reason;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "moved_at", nullable = false)
    private OffsetDateTime movedAt;

    protected StockMovement() {
    }

    public StockMovement(String itemCode, MovementType type, BigDecimal quantity, String reason, String userName, OffsetDateTime movedAt) {
        this.itemCode = itemCode;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.userName = userName;
        this.movedAt = movedAt;
    }

    public Long getId() { return id; }
    public String getItemCode() { return itemCode; }
    public MovementType getType() { return type; }
    public BigDecimal getQuantity() { return quantity; }
    public String getReason() { return reason; }
    public String getUserName() { return userName; }
    public OffsetDateTime getMovedAt() { return movedAt; }
}
