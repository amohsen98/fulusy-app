package com.fulusy.goal;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "goals")
public class Goal extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(nullable = false)
    public String name;

    public String icon;

    @Column(name = "target_amount", nullable = false, precision = 15, scale = 2)
    public BigDecimal targetAmount;

    @Column(name = "starting_amount", nullable = false, precision = 15, scale = 2)
    public BigDecimal startingAmount = BigDecimal.ZERO;

    @Column(name = "current_amount", nullable = false, precision = 15, scale = 2)
    public BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    public LocalDate deadline;

    @Column(nullable = false)
    public String priority = "medium";

    @Column(nullable = false)
    public String status = "active";

    @Column(name = "achieved_at")
    public LocalDate achievedAt;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static List<Goal> findActiveByUser(Long userId) {
        return list("userId = ?1 and status = 'active'",
                Sort.by("priority").descending().and("deadline"),
                userId);
    }

    public static List<Goal> findByUser(Long userId) {
        return list("userId", Sort.by("createdAt").descending(), userId);
    }
}
