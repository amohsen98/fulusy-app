package com.fulusy.savings;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "savings_contributions")
public class SavingsContribution extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    public BigDecimal amount;

    @Column(nullable = false)
    public String source;  // manual|month_rollover|goal_contribution

    @Column(length = 500)
    public String note;

    @Column(name = "contribution_date", nullable = false)
    public LocalDate contributionDate;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public static List<SavingsContribution> findByUser(Long userId) {
        return list("userId", Sort.by("contributionDate").descending(), userId);
    }
}
