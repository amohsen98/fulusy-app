package com.fulusy.income;

import com.fulusy.common.exception.NotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class IncomeService {

    @Transactional
    public Income create(Long userId, IncomeRequest req) {
        Income i = new Income();
        i.userId = userId;
        i.amount = req.amount();
        i.source = req.source();
        i.note = req.note();
        i.incomeDate = req.incomeDate();
        i.persist();
        return i;
    }

    public List<Income> listByUser(Long userId) {
        return Income.findByUser(userId);
    }

    public Income getById(Long userId, Long incomeId) {
        Income i = Income.findById(incomeId);
        if (i == null || !i.userId.equals(userId)) {
            throw new NotFoundException("Income not found");
        }
        return i;
    }

    @Transactional
    public void delete(Long userId, Long incomeId) {
        Income i = getById(userId, incomeId);
        i.delete();
    }
}
