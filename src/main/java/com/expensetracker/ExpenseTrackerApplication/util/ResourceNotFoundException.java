package com.expensetracker.ExpenseTrackerApplication.util;
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}