package com.example.stock.movement;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovementResponse(Long id, String itemCode, MovementType type, BigDecimal quantity, String reason,
                               String userName, OffsetDateTime movedAt, BigDecimal stockAfter) {
    public static MovementResponse from(StockMovement m, BigDecimal stockAfter) {
        return new MovementResponse(m.getId(), m.getItemCode(), m.getType(), m.getQuantity(), m.getReason(),
                m.getUserName(), m.getMovedAt(), stockAfter);
    }
}
