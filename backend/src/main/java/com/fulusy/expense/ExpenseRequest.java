package com.fulusy.expense;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Pattern(regexp = "essentials|transport|luxuries|shopping|other") String categoryId,
        @Size(max = 500) String note,
        @NotNull LocalDate expenseDate,
        Boolean isRecurring
) {}
