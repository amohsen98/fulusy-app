package com.fulusy.income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IncomeResponse(
        Long id,
        BigDecimal amount,
        String source,
        String note,
        LocalDate incomeDate,
        LocalDateTime createdAt
) {
    public static IncomeResponse from(Income i) {
        return new IncomeResponse(i.id, i.amount, i.source, i.note, i.incomeDate, i.createdAt);
    }
}
