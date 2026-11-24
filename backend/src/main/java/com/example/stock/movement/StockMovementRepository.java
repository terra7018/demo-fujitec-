package com.example.stock.movement;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findTop100ByOrderByMovedAtDescIdDesc();

    List<StockMovement> findTop100ByItemCodeOrderByMovedAtDescIdDesc(String itemCode);
}
