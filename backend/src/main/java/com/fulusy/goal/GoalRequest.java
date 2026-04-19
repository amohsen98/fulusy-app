package com.fulusy.goal;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 20) String icon,
        @NotNull @DecimalMin("0.01") BigDecimal targetAmount,
        BigDecimal startingAmount,
        @NotNull LocalDate deadline,
        @Pattern(regexp = "low|medium|high") String priority
) {}
