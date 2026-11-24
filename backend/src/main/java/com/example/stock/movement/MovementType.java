package com.example.stock.movement;

public enum MovementType {
    IN("E"),
    OUT("S");

    private final String legacyCode;

    MovementType(String legacyCode) {
        this.legacyCode = legacyCode;
    }

    public String legacyCode() {
        return legacyCode;
    }

    public static MovementType fromLegacyCode(String code) {
        for (MovementType t : values()) {
            if (t.legacyCode.equals(code)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown movement type: " + code);
    }
}
