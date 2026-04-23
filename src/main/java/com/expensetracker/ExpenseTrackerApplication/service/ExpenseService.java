package com.expensetracker.ExpenseTrackerApplication.service;

import com.expensetracker.ExpenseTrackerApplication.model.Expense;
import com.expensetracker.ExpenseTrackerApplication.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository repository;

    // Idempotency storage (simple memory map)
    private Map<String, Expense> idempotencyMap = new HashMap<>();

    public Expense createExpense(String key, Expense expense) {

        if (key != null && idempotencyMap.containsKey(key)) {
            return idempotencyMap.get(key);
        }

        Expense saved = repository.save(expense);

        if (key != null) {
            idempotencyMap.put(key, saved);
        }

        return saved;
    }

    public List<Expense> getExpenses(String category) {

        List<Expense> list;

        if (category != null && !category.isEmpty()) {
            list = repository.findByCategory(category);
        } else {
            list = repository.findAll();
        }

        // sort latest first
        list.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        return list;
    }
    
    public List<Expense> saveAll(List<Expense> expenses) {
        return repository.saveAll(expenses);
    }
}