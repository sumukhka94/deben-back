package com.sumukh.debenback.controllers;

import com.sumukh.debenback.dtos.CreateExpenseRequest;
import com.sumukh.debenback.dtos.CreateSettlementRequest;
import com.sumukh.debenback.entities.Expense;
import com.sumukh.debenback.entities.Settlement;
import com.sumukh.debenback.services.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    @PostMapping
    public ResponseEntity<Expense> addExpense(
            @PathVariable Long groupId,
            @RequestBody CreateExpenseRequest request) {
        Expense expense = expenseService.addExpense(groupId, request);
        return ResponseEntity.ok(expense);
    }
    
    @PostMapping("/settlements")
    public ResponseEntity<Settlement> recordSettlement(
            @PathVariable Long groupId,
            @RequestBody CreateSettlementRequest request) {
        Settlement settlement = expenseService.recordSettlement(groupId, request);
        return ResponseEntity.ok(settlement);
    }
}
