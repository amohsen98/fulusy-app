package com.fulusy.goal;

import com.fulusy.common.exception.BadRequestException;
import com.fulusy.common.exception.NotFoundException;
import com.fulusy.savings.SavingsContribution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class GoalService {

    @Transactional
    public Goal create(Long userId, GoalRequest req) {
        if (req.deadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("Deadline must be in the future");
        }

        Goal g = new Goal();
        g.userId = userId;
        g.name = req.name();
        g.icon = req.icon();
        g.targetAmount = req.targetAmount();
        g.startingAmount = req.startingAmount() != null ? req.startingAmount() : BigDecimal.ZERO;
        g.currentAmount = g.startingAmount;
        g.deadline = req.deadline();
        g.priority = req.priority() != null ? req.priority() : "medium";
        g.status = "active";
        g.persist();
        return g;
    }

    public List<Goal> listActive(Long userId) {
        return Goal.findActiveByUser(userId);
    }

    public List<Goal> listAll(Long userId) {
        return Goal.findByUser(userId);
    }

    public Goal getById(Long userId, Long goalId) {
        Goal g = Goal.findById(goalId);
        if (g == null || !g.userId.equals(userId)) {
            throw new NotFoundException("Goal not found");
        }
        return g;
    }

    /**
     * Contribute money to a goal. Also writes to savings_contributions
     * in the same transaction (per PRD invariant §5.1).
     */
    @Transactional
    public Goal contribute(Long userId, Long goalId, ContributeRequest req) {
        Goal g = getById(userId, goalId);
        if (!"active".equals(g.status)) {
            throw new BadRequestException("Can only contribute to active goals");
        }

        // 1. Write goal_contribution
        GoalContribution gc = new GoalContribution();
        gc.goalId = goalId;
        gc.amount = req.amount();
        gc.contributionDate = req.contributionDate();
        gc.note = req.note();
        gc.persist();

        // 2. Update goal current_amount
        g.currentAmount = g.currentAmount.add(req.amount());

        // 3. Check if goal achieved
        if (g.currentAmount.compareTo(g.targetAmount) >= 0) {
            g.status = "achieved";
            g.achievedAt = LocalDate.now();
        }
        g.persist();

        // 4. Mirror to savings_contributions (invariant: every goal contribution = savings)
        SavingsContribution sc = new SavingsContribution();
        sc.userId = userId;
        sc.amount = req.amount();
        sc.source = "goal_contribution";
        sc.note = "Goal: " + g.name + (req.note() != null ? " - " + req.note() : "");
        sc.contributionDate = req.contributionDate();
        sc.persist();

        return g;
    }

    @Transactional
    public Goal updateStatus(Long userId, Long goalId, String newStatus) {
        Goal g = getById(userId, goalId);
        if (!List.of("active", "achieved", "cancelled", "failed").contains(newStatus)) {
            throw new BadRequestException("Invalid status: " + newStatus);
        }
        g.status = newStatus;
        if ("achieved".equals(newStatus)) {
            g.achievedAt = LocalDate.now();
        }
        g.persist();
        return g;
    }

    @Transactional
    public void delete(Long userId, Long goalId) {
        Goal g = getById(userId, goalId);
        g.delete();
    }
}
