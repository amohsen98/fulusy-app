package com.fulusy.dashboard;

import com.fulusy.expense.Budget;
import com.fulusy.expense.Expense;
import com.fulusy.goal.Goal;
import com.fulusy.income.Income;
import com.fulusy.savings.SavingsContribution;
import com.fulusy.user.GamificationState;
import com.fulusy.user.PenaltyState;
import com.fulusy.user.User;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class DashboardService {

    public DashboardResponse getDashboard(Long userId) {
        User user = User.findById(userId);
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        // Balance = starting + all income - all expenses - non-rollover savings
        BigDecimal totalIncome = Income.list("userId", userId).stream()
                .map(i -> ((Income) i).amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = Expense.list("userId", userId).stream()
                .map(e -> ((Expense) e).amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSavings = SavingsContribution.list("userId = ?1 and source != 'month_rollover'", userId)
                .stream().map(s -> ((SavingsContribution) s).amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal currentBalance = user.startingBalance.add(totalIncome)
                .subtract(totalExpenses).subtract(totalSavings);

        // This month spending
        List<Expense> monthExpenses = Expense.findByUserInMonth(userId, monthStart, monthEnd);
        BigDecimal monthSpent = monthExpenses.stream()
                .map(e -> e.amount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> byCategory = monthExpenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.categoryId,
                        Collectors.reducing(BigDecimal.ZERO, e -> e.amount, BigDecimal::add)));

        String topCategory = byCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        // This month income
        BigDecimal monthIncome = Income.list("userId = ?1 and incomeDate >= ?2 and incomeDate <= ?3",
                        userId, monthStart, monthEnd).stream()
                .map(i -> ((Income) i).amount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // This month savings
        BigDecimal monthSaved = SavingsContribution.list(
                        "userId = ?1 and contributionDate >= ?2 and contributionDate <= ?3",
                        userId, monthStart, monthEnd).stream()
                .map(s -> ((SavingsContribution) s).amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Budget health bars
        List<Budget> userBudgets = Budget.findByUser(userId);
        List<DashboardResponse.BudgetStatus> budgetStatuses = userBudgets.stream()
                .map(b -> {
                    BigDecimal spent = byCategory.getOrDefault(b.categoryId, BigDecimal.ZERO);
                    BigDecimal pct = b.monthlyLimit.compareTo(BigDecimal.ZERO) > 0
                            ? spent.divide(b.monthlyLimit, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(1, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return new DashboardResponse.BudgetStatus(b.categoryId, b.monthlyLimit, spent, pct);
                }).toList();

        // Goals aggregate progress
        List<Goal> activeGoals = Goal.findActiveByUser(userId);
        BigDecimal goalsTarget = activeGoals.stream()
                .map(g -> g.targetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal goalsCurrent = activeGoals.stream()
                .map(g -> g.currentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal goalsProgress = goalsTarget.compareTo(BigDecimal.ZERO) > 0
                ? goalsCurrent.divide(goalsTarget, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Gamification
        GamificationState gs = GamificationState.findByUserId(userId);
        int streak = gs != null ? gs.currentStreak : 0;
        int longestStreak = gs != null ? gs.longestStreak : 0;
        int daysLogged = gs != null ? gs.totalDaysLogged : 0;

        // Penalty
        PenaltyState ps = PenaltyState.findByUserId(userId);
        int strikes = ps != null ? ps.strikeCount : 0;
        boolean penaltyScreen = ps != null && ps.penaltyScreenRequired == 1;
        boolean chatLocked = ps != null && ps.chatbotLockedUntil != null
                && ps.chatbotLockedUntil.isAfter(LocalDateTime.now());
        boolean quickLocked = ps != null && ps.quickaddLockedUntil != null
                && ps.quickaddLockedUntil.isAfter(LocalDateTime.now());

        return new DashboardResponse(
                currentBalance, user.startingBalance,
                monthSpent, monthIncome, monthSaved,
                byCategory, topCategory,
                budgetStatuses,
                goalsProgress, goalsCurrent, goalsTarget, activeGoals.size(),
                streak, longestStreak, daysLogged,
                strikes, penaltyScreen, chatLocked, quickLocked
        );
    }
}
