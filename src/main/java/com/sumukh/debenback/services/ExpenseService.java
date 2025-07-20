package com.sumukh.debenback.services;

import com.sumukh.debenback.dtos.CreateExpenseRequest;
import com.sumukh.debenback.dtos.CreateSettlementRequest;
import com.sumukh.debenback.entities.*;
import com.sumukh.debenback.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final SettlementRepository settlementRepository;
    
    @Transactional
    public Expense addExpense(Long groupId, CreateExpenseRequest request) {
        // Validation
        validateExpenseRequest(request);
        
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));
        
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setTitle(request.getTitle());
        expense.setDescription(request.getDescription());
        expense.setTotalAmount(request.getTotalAmount());
        
        expense = expenseRepository.save(expense);
        
        // Create payers
        List<ExpensePayer> payers = new ArrayList<>();
        for (CreateExpenseRequest.PayerRequest payerReq : request.getPayers()) {
            User user = userRepository.findById(payerReq.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            ExpensePayerId payerId = new ExpensePayerId();
            payerId.setExpenseId(expense.getId());
            payerId.setUserId(user.getId());
            
            ExpensePayer payer = new ExpensePayer();
            payer.setId(payerId);
            payer.setExpense(expense);
            payer.setUser(user);
            payer.setAmountPaid(payerReq.getAmountPaid());
            payers.add(payer);
        }
        expense.setPayers(payers);
        
        // Create splits
        List<ExpenseSplit> splits = new ArrayList<>();
        for (Long splitterId : request.getSplitters()) {
            User user = userRepository.findById(splitterId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            ExpenseSplitId splitId = new ExpenseSplitId();
            splitId.setExpenseId(expense.getId());
            splitId.setUserId(user.getId());
            
            ExpenseSplit split = new ExpenseSplit();
            split.setId(splitId);
            split.setExpense(expense);
            split.setUser(user);
            
            Double amountOwed = calculateAmountOwed(request, splitterId);
            split.setAmountOwed(amountOwed);
            splits.add(split);
        }
        expense.setSplits(splits);
        
        return expenseRepository.save(expense);
    }
    
    private void validateExpenseRequest(CreateExpenseRequest request) {
        if (request.getTotalAmount() == null || request.getTotalAmount() <= 0) {
            throw new IllegalArgumentException("Total amount must be positive");
        }
        
        if (request.getPayers() == null || request.getPayers().isEmpty()) {
            throw new IllegalArgumentException("At least one payer is required");
        }
        
        if (request.getSplitters() == null || request.getSplitters().isEmpty()) {
            throw new IllegalArgumentException("At least one splitter is required");
        }
        
        // Validate total paid amount
        double totalPaid = request.getPayers().stream()
            .mapToDouble(CreateExpenseRequest.PayerRequest::getAmountPaid)
            .sum();
        
        if (Math.abs(totalPaid - request.getTotalAmount()) > 0.01) {
            throw new IllegalArgumentException(
                String.format("Total paid amount (%.2f) must equal total expense amount (%.2f)", 
                    totalPaid, request.getTotalAmount()));
        }
        
        // Validate split amounts based on type
        if ("percentage".equals(request.getSplitType())) {
            validatePercentageSplits(request);
        } else if ("amount".equals(request.getSplitType())) {
            validateAmountSplits(request);
        }
    }
    
    private void validatePercentageSplits(CreateExpenseRequest request) {
        if (request.getCustomSplits() == null || request.getCustomSplits().isEmpty()) {
            throw new IllegalArgumentException("Custom splits required for percentage split type");
        }
        
        double totalPercentage = request.getCustomSplits().stream()
            .mapToDouble(CreateExpenseRequest.CustomSplitRequest::getPercentage)
            .sum();
        
        if (Math.abs(totalPercentage - 100.0) > 0.01) {
            throw new IllegalArgumentException(
                String.format("Total percentage (%.2f%%) must equal 100%%", totalPercentage));
        }
    }
    
    private void validateAmountSplits(CreateExpenseRequest request) {
        if (request.getCustomAmounts() == null || request.getCustomAmounts().isEmpty()) {
            throw new IllegalArgumentException("Custom amounts required for amount split type");
        }
        
        double totalSplitAmount = request.getCustomAmounts().stream()
            .mapToDouble(CreateExpenseRequest.CustomAmountRequest::getAmount)
            .sum();
        
        if (Math.abs(totalSplitAmount - request.getTotalAmount()) > 0.01) {
            throw new IllegalArgumentException(
                String.format("Total split amount (%.2f) must equal total expense amount (%.2f)", 
                    totalSplitAmount, request.getTotalAmount()));
        }
    }
    
    private Double calculateAmountOwed(CreateExpenseRequest request, Long userId) {
        if ("equal".equals(request.getSplitType())) {
            if (request.getSplitters().isEmpty()) {
                throw new IllegalArgumentException("No splitters found for equal split");
            }
            return request.getTotalAmount() / request.getSplitters().size();
        } else if ("percentage".equals(request.getSplitType()) && request.getCustomSplits() != null) {
            return request.getCustomSplits().stream()
                .filter(split -> split.getUserId().equals(userId))
                .findFirst()
                .map(split -> request.getTotalAmount() * split.getPercentage() / 100)
                .orElse(0.0);
        } else if ("amount".equals(request.getSplitType()) && request.getCustomAmounts() != null) {
            return request.getCustomAmounts().stream()
                .filter(amount -> amount.getUserId().equals(userId))
                .findFirst()
                .map(CreateExpenseRequest.CustomAmountRequest::getAmount)
                .orElse(0.0);
        }
        return 0.0;
    }
    
    @Transactional
    public Settlement recordSettlement(Long groupId, CreateSettlementRequest request) {
        // Validation
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Settlement amount must be positive");
        }
        
        if (request.getFromUserId().equals(request.getToUserId())) {
            throw new IllegalArgumentException("From user and to user cannot be the same");
        }
        
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User fromUser = userRepository.findById(request.getFromUserId())
            .orElseThrow(() -> new RuntimeException("From user not found"));
        
        User toUser = userRepository.findById(request.getToUserId())
            .orElseThrow(() -> new RuntimeException("To user not found"));
        
        Settlement settlement = new Settlement();
        settlement.setGroup(group);
        settlement.setFromUser(fromUser);
        settlement.setToUser(toUser);
        settlement.setAmount(BigDecimal.valueOf(request.getAmount()));
        settlement.setDescription(request.getDescription());
        
        // Handle custom settlement date if provided
        if (request.getDate() != null && !request.getDate().trim().isEmpty()) {
            try {
                LocalDateTime customDate = LocalDateTime.parse(request.getDate() + "T00:00:00");
                settlement.setSettledAt(customDate);
            } catch (Exception e) {
                // If parsing fails, use current time (default behavior)
                settlement.setSettledAt(LocalDateTime.now());
            }
        }
        
        return settlementRepository.save(settlement);
    }
}
