package com.example.stock.movement;

import com.example.stock.item.Item;
import com.example.stock.item.ItemNotFoundException;
import com.example.stock.item.ItemRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 入庫・出庫の登録。旧 unitEntradaMat / unitSaidaMat の BtnGravarClick に相当する。
 * 旧実装では移動履歴の保存と在庫数の更新が別々に確定していたため、1 トランザクションにまとめ、
 * 品目行をロックして同時出庫でも在庫がマイナスにならないようにしている。
 */
@Service
public class StockMovementService {
    private final ItemRepository items;
    private final StockMovementRepository movements;
    private final Clock clock;

    public StockMovementService(ItemRepository items, StockMovementRepository movements, Clock clock) {
        this.items = items;
        this.movements = movements;
        this.clock = clock;
    }

    @Transactional
    public MovementResponse register(MovementRequest request) {
        Item item = items.findForUpdate(request.itemCode())
                .orElseThrow(() -> new ItemNotFoundException(request.itemCode()));

        switch (request.type()) {
            case IN -> item.receive(request.quantity());
            case OUT -> item.issue(request.quantity());
        }

        StockMovement saved = movements.save(new StockMovement(item.getCode(), request.type(), request.quantity(),
                request.reason(), request.userName(), OffsetDateTime.now(clock)));
        return MovementResponse.from(saved, item.getQuantity());
    }

    @Transactional(readOnly = true)
    public List<StockMovement> recent(String itemCode) {
        return itemCode == null || itemCode.isBlank()
                ? movements.findTop100ByOrderByMovedAtDescIdDesc()
                : movements.findTop100ByItemCodeOrderByMovedAtDescIdDesc(itemCode);
    }
}
