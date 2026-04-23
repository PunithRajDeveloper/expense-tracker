package com.expensetracker.ExpenseTrackerApplication.repository;

import com.expensetracker.ExpenseTrackerApplication.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCategory(String category);

    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
    
    List<Expense> findByAmountBetween(BigDecimal min, BigDecimal max);
    
    List<Expense> findByDescriptionContainingIgnoreCase(String keyword);
    
}