package com.fulusy.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        BigDecimal amount,
        String categoryId,
        String note,
        LocalDate expenseDate,
        boolean isRecurring,
        LocalDateTime createdAt
) {
    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(
                e.id, e.amount, e.categoryId, e.note,
                e.expenseDate, e.isRecurring != null && e.isRecurring == 1,
                e.createdAt
        );
    }
}
