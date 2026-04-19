package com.fulusy.savings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SavingsResponse(
        Long id,
        BigDecimal amount,
        String source,
        String note,
        LocalDate contributionDate,
        LocalDateTime createdAt
) {
    public static SavingsResponse from(SavingsContribution s) {
        return new SavingsResponse(s.id, s.amount, s.source, s.note, s.contributionDate, s.createdAt);
    }
}
