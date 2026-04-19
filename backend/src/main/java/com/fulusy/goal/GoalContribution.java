package com.fulusy.goal;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "goal_contributions")
public class GoalContribution extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "goal_id", nullable = false)
    public Long goalId;

    @Column(nullable = false, precision = 15, scale = 2)
    public BigDecimal amount;

    @Column(name = "contribution_date", nullable = false)
    public LocalDate contributionDate;

    @Column(length = 500)
    public String note;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public static List<GoalContribution> findByGoal(Long goalId) {
        return list("goalId", Sort.by("contributionDate").descending(), goalId);
    }
}
