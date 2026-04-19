package com.fulusy.auth;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 100) String name,
        @NotNull @DecimalMin("0.00") BigDecimal startingBalance,
        @NotBlank @Pattern(regexp = "fixed|variable|hybrid") String incomeMode,
        BigDecimal fixedIncomeAmount,
        @Min(1) @Max(31) Integer fixedIncomeDay,
        @Pattern(regexp = "ar|en") String language
) {}
