package com.example.stock.item;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemRepository items;

    public ItemController(ItemRepository items) {
        this.items = items;
    }

    @GetMapping
    public List<ItemResponse> list() {
        return items.findAll(Sort.by("code")).stream().map(ItemResponse::from).toList();
    }

    @GetMapping("/{code}")
    public ItemResponse get(@PathVariable String code) {
        return items.findById(code).map(ItemResponse::from).orElseThrow(() -> new ItemNotFoundException(code));
    }
}
