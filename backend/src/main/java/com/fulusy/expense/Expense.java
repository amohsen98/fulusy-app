package com.fulusy.expense;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "expenses")
public class Expense extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    public BigDecimal amount;

    @Column(name = "category_id", nullable = false)
    public String categoryId;  // essentials|transport|luxuries|shopping|other

    @Column(length = 500)
    public String note;

    @Column(name = "expense_date", nullable = false)
    public LocalDate expenseDate;

    @Column(name = "is_recurring", nullable = false)
    public Short isRecurring = 0;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public static List<Expense> findByUser(Long userId) {
        return list("userId", Sort.by("expenseDate").descending(), userId);
    }

    public static List<Expense> findByUserInMonth(Long userId, LocalDate monthStart, LocalDate monthEnd) {
        return list("userId = ?1 and expenseDate >= ?2 and expenseDate <= ?3",
                Sort.by("expenseDate").descending(),
                userId, monthStart, monthEnd);
    }
}
