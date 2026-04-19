package com.fulusy.expense;

import com.fulusy.common.exception.NotFoundException;
import com.fulusy.user.GamificationState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ExpenseService {

    @Transactional
    public Expense create(Long userId, ExpenseRequest req) {
        Expense e = new Expense();
        e.userId = userId;
        e.amount = req.amount();
        e.categoryId = req.categoryId();
        e.note = req.note();
        e.expenseDate = req.expenseDate();
        e.isRecurring = (req.isRecurring() != null && req.isRecurring()) ? (short) 1 : 0;
        e.persist();

        updateStreak(userId, req.expenseDate());
        return e;
    }

    public List<Expense> listByUser(Long userId) {
        return Expense.findByUser(userId);
    }

    public List<Expense> listByUserInMonth(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return Expense.findByUserInMonth(userId, start, end);
    }

    public Expense getById(Long userId, Long expenseId) {
        Expense e = Expense.findById(expenseId);
        if (e == null || !e.userId.equals(userId)) {
            throw new NotFoundException("Expense not found");
        }
        return e;
    }

    @Transactional
    public Expense update(Long userId, Long expenseId, ExpenseRequest req) {
        Expense e = getById(userId, expenseId);
        e.amount = req.amount();
        e.categoryId = req.categoryId();
        e.note = req.note();
        e.expenseDate = req.expenseDate();
        e.isRecurring = (req.isRecurring() != null && req.isRecurring()) ? (short) 1 : 0;
        e.persist();
        return e;
    }

    @Transactional
    public void delete(Long userId, Long expenseId) {
        Expense e = getById(userId, expenseId);
        e.delete();
    }

    private void updateStreak(Long userId, LocalDate logDate) {
        GamificationState gs = GamificationState.findByUserId(userId);
        if (gs == null) return;

        LocalDate today = logDate != null ? logDate : LocalDate.now();

        if (gs.lastLogDate == null) {
            gs.currentStreak = 1;
        } else if (gs.lastLogDate.equals(today)) {
            // same day, no change
            return;
        } else if (gs.lastLogDate.plusDays(1).equals(today)) {
            gs.currentStreak++;
        } else {
            gs.currentStreak = 1;
        }

        if (gs.currentStreak > gs.longestStreak) {
            gs.longestStreak = gs.currentStreak;
        }
        gs.totalDaysLogged++;
        gs.lastLogDate = today;
        gs.persist();
    }
}
