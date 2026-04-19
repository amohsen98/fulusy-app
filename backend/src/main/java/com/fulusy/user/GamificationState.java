package com.fulusy.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gamification_state")
public class GamificationState extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    public Long userId;

    @Column(name = "current_streak", nullable = false)
    public Integer currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    public Integer longestStreak = 0;

    @Column(name = "total_days_logged", nullable = false)
    public Integer totalDaysLogged = 0;

    @Column(name = "under_budget_days", nullable = false)
    public Integer underBudgetDays = 0;

    @Column(name = "months_completed", nullable = false)
    public Integer monthsCompleted = 0;

    @Column(name = "last_log_date")
    public LocalDate lastLogDate;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static GamificationState findByUserId(Long userId) {
        return find("userId", userId).firstResult();
    }
}
