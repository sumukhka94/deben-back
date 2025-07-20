package com.sumukh.debenback.controllers;

import com.sumukh.debenback.dtos.CreateExpenseRequest;
import com.sumukh.debenback.dtos.CreateSettlementRequest;
import com.sumukh.debenback.entities.Expense;
import com.sumukh.debenback.entities.Settlement;
import com.sumukh.debenback.services.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    @PostMapping
    public ResponseEntity<?> addExpense(
            @PathVariable Long groupId,
            @RequestBody CreateExpenseRequest request) {
        try {
            Expense expense = expenseService.addExpense(groupId, request);
            return ResponseEntity.ok(expense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PostMapping("/settlements")
    public ResponseEntity<?> recordSettlement(
            @PathVariable Long groupId,
            @RequestBody CreateSettlementRequest request) {
        try {
            Settlement settlement = expenseService.recordSettlement(groupId, request);
            return ResponseEntity.ok(settlement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
