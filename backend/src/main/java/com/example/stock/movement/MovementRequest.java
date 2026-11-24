package com.example.stock.movement;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record MovementRequest(
        @NotBlank(message = "品目コードは必須です") String itemCode,
        @NotNull(message = "入出庫区分は必須です") MovementType type,
        @NotNull(message = "数量は必須です")
        @DecimalMin(value = "0", inclusive = false, message = "数量は0より大きい値を入力してください")
        @Digits(integer = 11, fraction = 3, message = "数量は小数点以下3桁までです") BigDecimal quantity,
        @Size(max = 500, message = "理由は500文字以内です") String reason,
        @NotBlank(message = "担当者は必須です") @Size(max = 50) String userName) {
}
