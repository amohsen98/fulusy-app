package com.fulusy.goal;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ContributeRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @Size(max = 500) String note,
        @NotNull LocalDate contributionDate
) {}
