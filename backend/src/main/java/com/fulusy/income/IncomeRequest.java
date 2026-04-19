package com.fulusy.income;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Pattern(regexp = "salary|freelance|gift|gam3eya_payout|bonus|other") String source,
        @Size(max = 500) String note,
        @NotNull LocalDate incomeDate
) {}
