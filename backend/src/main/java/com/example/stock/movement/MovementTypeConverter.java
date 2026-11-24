package com.example.stock.movement;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MovementTypeConverter implements AttributeConverter<MovementType, String> {
    @Override
    public String convertToDatabaseColumn(MovementType type) {
        return type == null ? null : type.legacyCode();
    }

    @Override
    public MovementType convertToEntityAttribute(String code) {
        return code == null ? null : MovementType.fromLegacyCode(code);
    }
}
