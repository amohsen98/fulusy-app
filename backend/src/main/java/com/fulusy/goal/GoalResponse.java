package com.fulusy.goal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record GoalResponse(
        Long id,
        String name,
        String icon,
        BigDecimal targetAmount,
        BigDecimal startingAmount,
        BigDecimal currentAmount,
        LocalDate deadline,
        String priority,
        String status,
        LocalDate achievedAt,
        BigDecimal progressPct,
        BigDecimal requiredMonthlySavings,
        long daysRemaining,
        LocalDateTime createdAt
) {
    public static GoalResponse from(Goal g) {
        BigDecimal progress = BigDecimal.ZERO;
        if (g.targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            progress = g.currentAmount
                    .divide(g.targetAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
        }

        BigDecimal remaining = g.targetAmount.subtract(g.currentAmount).max(BigDecimal.ZERO);
        long daysLeft = Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), g.deadline));
        double monthsLeft = Math.max(0.5, daysLeft / 30.44);
        BigDecimal requiredMonthly = remaining.divide(
                BigDecimal.valueOf(monthsLeft), 2, RoundingMode.CEILING);

        return new GoalResponse(
                g.id, g.name, g.icon, g.targetAmount, g.startingAmount,
                g.currentAmount, g.deadline, g.priority, g.status,
                g.achievedAt, progress, requiredMonthly, daysLeft, g.createdAt
        );
    }
}
