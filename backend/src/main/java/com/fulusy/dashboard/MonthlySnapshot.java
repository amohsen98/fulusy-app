package com.fulusy.dashboard;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_snapshots")
public class MonthlySnapshot extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(name = "month_key", nullable = false)
    public String monthKey;  // "YYYY-MM"

    @Column(name = "total_spent", nullable = false, precision = 15, scale = 2)
    public BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "total_income", nullable = false, precision = 15, scale = 2)
    public BigDecimal totalIncome = BigDecimal.ZERO;

    @Column(name = "total_saved", nullable = false, precision = 15, scale = 2)
    public BigDecimal totalSaved = BigDecimal.ZERO;

    @Column(name = "auto_rolled_to_savings", nullable = false, precision = 15, scale = 2)
    public BigDecimal autoRolledToSavings = BigDecimal.ZERO;

    @Column(name = "essentials_spent", nullable = false, precision = 15, scale = 2)
    public BigDecimal essentialsSpent = BigDecimal.ZERO;

    @Column(name = "transport_spent", nullable = false, precision = 15, scale = 2)
    public BigDecimal transportSpent = BigDecimal.ZERO;

    @Column(name = "luxuries_spent", nullable = false, precision = 15, scale = 2)
    public BigDecimal luxuriesSpent = BigDecimal.ZERO;

    @Column(name = "shopping_spent", nullable = false, precision = 15, scale = 2)
    public BigDecimal shoppingSpent = BigDecimal.ZERO;

    @Column(name = "other_spent", nullable = false, precision = 15, scale = 2)
    public BigDecimal otherSpent = BigDecimal.ZERO;

    @Column(name = "start_balance", nullable = false, precision = 15, scale = 2)
    public BigDecimal startBalance;

    @Column(name = "end_balance", nullable = false, precision = 15, scale = 2)
    public BigDecimal endBalance;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
