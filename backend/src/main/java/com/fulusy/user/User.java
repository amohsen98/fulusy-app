package com.fulusy.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @Column(nullable = false)
    public String name;

    @Column(name = "starting_balance", nullable = false, precision = 15, scale = 2)
    public BigDecimal startingBalance = BigDecimal.ZERO;

    @Column(name = "income_mode", nullable = false)
    public String incomeMode;  // "fixed" | "variable" | "hybrid"

    @Column(name = "fixed_income_amount", precision = 15, scale = 2)
    public BigDecimal fixedIncomeAmount;

    @Column(name = "fixed_income_day")
    public Integer fixedIncomeDay;

    @Column(nullable = false)
    public String currency = "EGP";

    @Column(nullable = false)
    public String language = "ar";

    @Column(name = "visual_style", nullable = false)
    public String visualStyle = "minimalist";

    @Column(name = "monthly_savings_goal", precision = 15, scale = 2)
    public BigDecimal monthlySavingsGoal;

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

    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
