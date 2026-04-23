package com.expensetracker.ExpenseTrackerApplication.controller;

import com.expensetracker.ExpenseTrackerApplication.model.Expense;
import com.expensetracker.ExpenseTrackerApplication.repository.ExpenseRepository;
import com.expensetracker.ExpenseTrackerApplication.service.ExpenseService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/expenses")
@CrossOrigin
public class ExpenseController {

    @Autowired
    private ExpenseService service;
    
    @Autowired
    private ExpenseRepository repository;

    @PostMapping
    public ResponseEntity<Expense> createExpense(
            @RequestHeader(value = "Idempotency-Key", required = false) String key,
            @Valid @RequestBody Expense expense) {

        return ResponseEntity.ok(service.createExpense(key, expense));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Expense>> createBulkExpenses(
            @RequestBody List<Expense> expenses) {

        List<Expense> saved = service.saveAll(expenses);
        return ResponseEntity.ok(saved);
    }
    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(
            @RequestParam(required = false) String category) {

        return ResponseEntity.ok(service.getExpenses(category));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable Long id) {
        return ResponseEntity.of(repository.findById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Expense> update(
            @PathVariable Long id,
            @RequestBody Expense updated) {

        Expense exp = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        exp.setAmount(updated.getAmount());
        exp.setCategory(updated.getCategory());
        exp.setDescription(updated.getDescription());
        exp.setDate(updated.getDate());

        return ResponseEntity.ok(repository.save(exp));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/date-range")
    public List<Expense> getByDateRange(
            @RequestParam String start,
            @RequestParam String end) {

        return repository.findByDateBetween(
            LocalDate.parse(start),
            LocalDate.parse(end)
        );
    }
    
    @GetMapping("/amount-range")
    public List<Expense> getByAmountRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {

        return repository.findByAmountBetween(min, max);
    }
    
    @GetMapping("/total")
    public BigDecimal getTotal() {
        return repository.findAll().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @GetMapping("/total-by-category")
    public Map<String, BigDecimal> totalByCategory() {

        return repository.findAll().stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                Collectors.mapping(
                    Expense::getAmount,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                )
            ));
    }
    
    @GetMapping("/paged")
    public Page<Expense> getPaged(
            @RequestParam int page,
            @RequestParam int size) {

        return repository.findAll(PageRequest.of(page, size));
    }
    
    @GetMapping("/sorted")
    public List<Expense> getSorted() {
        return repository.findAll(
            Sort.by(Sort.Direction.DESC, "date")
        );
    }
    
    @DeleteMapping("/bulk")
    public void deleteBulk(@RequestBody List<Long> ids) {
        repository.deleteAllById(ids);
    }
    
    @GetMapping("/search")
    public List<Expense> search(@RequestParam String keyword) {
        return repository.findByDescriptionContainingIgnoreCase(keyword);
    }
    
    @GetMapping("/top")
    public List<Expense> topExpenses() {
        return repository.findAll(
            PageRequest.of(0, 5, Sort.by("amount").descending())
        ).getContent();
    }
    
    @DeleteMapping("/all")
    public void deleteAll() {
        repository.deleteAll();
    }
}