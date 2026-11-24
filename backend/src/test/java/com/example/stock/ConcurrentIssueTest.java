package com.example.stock;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.stock.item.InsufficientStockException;
import com.example.stock.item.Item;
import com.example.stock.item.ItemRepository;
import com.example.stock.movement.MovementRequest;
import com.example.stock.movement.MovementType;
import com.example.stock.movement.StockMovementRepository;
import com.example.stock.movement.StockMovementService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfig.class)
class ConcurrentIssueTest {

    @Autowired StockMovementService service;
    @Autowired ItemRepository items;
    @Autowired StockMovementRepository movements;

    @Test
    @DisplayName("同時に20件出庫しても、在庫5を超えて出庫されずマイナスにならない")
    void concurrentIssuesNeverOversell() throws Exception {
        movements.deleteAll();
        items.deleteAll();
        items.save(new Item("C-001", "同時出庫テスト", "個", new BigDecimal("5"), BigDecimal.ZERO, null));

        int requests = 20;
        ExecutorService pool = Executors.newFixedThreadPool(requests);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < requests; i++) {
            results.add(pool.submit(() -> {
                start.await();
                try {
                    service.register(new MovementRequest("C-001", MovementType.OUT, BigDecimal.ONE, null, "worker"));
                    return true;
                } catch (InsufficientStockException e) {
                    return false;
                }
            }));
        }
        start.countDown();
        int succeeded = 0;
        for (Future<Boolean> r : results) {
            if (r.get()) {
                succeeded++;
            }
        }
        pool.shutdown();

        assertThat(succeeded).isEqualTo(5);
        assertThat(items.findById("C-001").orElseThrow().getQuantity()).isEqualByComparingTo("0");
        assertThat(movements.count()).isEqualTo(5);
    }
}
