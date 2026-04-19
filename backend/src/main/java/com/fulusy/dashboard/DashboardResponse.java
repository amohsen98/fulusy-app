package com.fulusy.dashboard;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        // Balance HUD
        BigDecimal currentBalance,
        BigDecimal startingBalance,

        // This month
        BigDecimal monthSpent,
        BigDecimal monthIncome,
        BigDecimal monthSaved,
        Map<String, BigDecimal> spendingByCategory,
        String topCategory,

        // Budget health bars
        List<BudgetStatus> budgets,

        // Goals progress bar
        BigDecimal goalsProgressPct,
        BigDecimal goalsCurrentTotal,
        BigDecimal goalsTargetTotal,
        int activeGoalsCount,

        // Gamification
        int currentStreak,
        int longestStreak,
        int totalDaysLogged,

        // Penalty
        int strikeCount,
        boolean penaltyScreenRequired,
        boolean chatbotLocked,
        boolean quickAddLocked
) {
    public record BudgetStatus(
            String categoryId,
            BigDecimal limit,
            BigDecimal spent,
            BigDecimal pct
    ) {}
}
