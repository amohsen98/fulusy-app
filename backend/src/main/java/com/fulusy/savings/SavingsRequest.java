package com.fulusy.savings;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingsRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Pattern(regexp = "manual|month_rollover|goal_contribution") String source,
        @Size(max = 500) String note,
        @NotNull LocalDate contributionDate
) {}
