package com.example.stock.item;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String code) {
        super("品目コードが登録されていません：" + code);
    }
}
