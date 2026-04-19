package com.fulusy.goal;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "adherence_snapshots")
public class AdherenceSnapshot extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "goal_id", nullable = false)
    public Long goalId;

    @Column(name = "check_in_date", nullable = false)
    public LocalDate checkInDate;

    @Column(name = "adherence_score", nullable = false, precision = 5, scale = 2)
    public BigDecimal adherenceScore;

    @Column(name = "expected_saved", nullable = false, precision = 15, scale = 2)
    public BigDecimal expectedSaved;

    @Column(name = "actual_saved", nullable = false, precision = 15, scale = 2)
    public BigDecimal actualSaved;

    public String trend;  // improving|declining|flat

    @Column(name = "top_spending_category")
    public String topSpendingCategory;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
