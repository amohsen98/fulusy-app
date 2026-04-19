package com.fulusy.user;

import java.math.BigDecimal;

public record UserProfileResponse(
        Long id,
        String email,
        String name,
        BigDecimal startingBalance,
        String incomeMode,
        BigDecimal fixedIncomeAmount,
        Integer fixedIncomeDay,
        String currency,
        String language,
        String visualStyle,
        BigDecimal monthlySavingsGoal
) {
    public static UserProfileResponse from(User u) {
        return new UserProfileResponse(
                u.id,
                u.email,
                u.name,
                u.startingBalance,
                u.incomeMode,
                u.fixedIncomeAmount,
                u.fixedIncomeDay,
                u.currency,
                u.language,
                u.visualStyle,
                u.monthlySavingsGoal
        );
    }
}
