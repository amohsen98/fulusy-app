package com.fulusy.expense;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record BudgetRequest(
        @NotBlank @Pattern(regexp = "essentials|transport|luxuries|shopping|other") String categoryId,
        @NotNull @DecimalMin("0.01") BigDecimal monthlyLimit
) {}
