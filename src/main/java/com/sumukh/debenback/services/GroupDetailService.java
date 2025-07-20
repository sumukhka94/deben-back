package com.sumukh.debenback.services;

import com.sumukh.debenback.dtos.*;
import com.sumukh.debenback.entities.*;
import com.sumukh.debenback.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupDetailService {
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;

    public GroupDetailDto getGroupDetail(Long groupId) {
        Group group = groupRepository.findByIdWithMembers(groupId)
            .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        return new GroupDetailDto(
            group.getId(),
            group.getName(),
            group.getDescription(),
            calculateBalances(group),
            getExpenses(groupId),
            getSettlements(groupId)
        );
    }

    private List<MemberBalanceDto> calculateBalances(Group group) {
        Map<Long, String> memberNames = group.getMembers().stream()
            .collect(Collectors.toMap(User::getId, User::getName));
        
        Map<Long, Double> balances = new HashMap<>();
        memberNames.keySet().forEach(id -> balances.put(id, 0.0));

        // Calculate from expenses
        List<Expense> expenses = expenseRepository.findByGroupId(group.getId());
        for (Expense expense : expenses) {
            // Add what members paid
            for (ExpensePayer payer : expense.getPayers()) {
                Long userId = payer.getUser().getId();
                balances.put(userId, balances.get(userId) + payer.getAmountPaid());
            }
            // Subtract what members owe
            for (ExpenseSplit split : expense.getSplits()) {
                Long userId = split.getUser().getId();
                balances.put(userId, balances.get(userId) - split.getAmountOwed());
            }
        }

        // Apply settlements
        List<Settlement> settlements = settlementRepository.findByGroupIdOrderBySettledAtDesc(group.getId());
        for (Settlement settlement : settlements) {
            double amount = settlement.getAmount().doubleValue();
            Long payerId = settlement.getFromUser().getId();
            Long receiverId = settlement.getToUser().getId();
            
            // When you pay: your debt reduces (balance increases)
            balances.put(payerId, balances.get(payerId) + amount);
            // When you receive: you're owed less (balance decreases)
            balances.put(receiverId, balances.get(receiverId) - amount);
        }

        return memberNames.entrySet().stream()
            .map(entry -> new MemberBalanceDto(
                entry.getKey(),
                entry.getValue(),
                "/placeholder.svg?height=40&width=40",
                balances.get(entry.getKey())
            ))
            .collect(Collectors.toList());
    }

    private List<ExpenseDto> getExpenses(Long groupId) {
        List<Expense> expenses = expenseRepository.findByGroupIdOrderByPaidAtDesc(groupId);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        return expenses.stream().map(expense -> {
            List<PaidByDto> paidBy = expense.getPayers().stream()
                .map(p -> new PaidByDto(p.getUser().getId(), p.getAmountPaid()))
                .collect(Collectors.toList());

            Map<Long, Double> paidMap = paidBy.stream()
                .collect(Collectors.toMap(PaidByDto::getUserId, PaidByDto::getAmount));

            List<ExpenseShareDto> splitAmong = expense.getSplits().stream()
                .map(split -> {
                    Long userId = split.getUser().getId();
                    double share = split.getAmountOwed();
                    double paid = paidMap.getOrDefault(userId, 0.0);
                    double owes = Math.max(share - paid, 0.0);
                    return new ExpenseShareDto(userId, share, paid, owes);
                })
                .collect(Collectors.toList());

            return new ExpenseDto(
                expense.getId(),
                expense.getTitle(),
                expense.getDescription(),
                expense.getPaidAt().toLocalDate().format(formatter),
                expense.getTotalAmount(),
                paidBy,
                splitAmong
            );
        }).collect(Collectors.toList());
    }

    private List<SettlementDto> getSettlements(Long groupId) {
        return settlementRepository.findByGroupIdOrderBySettledAtDesc(groupId).stream()
            .map(s -> new SettlementDto(
                s.getId(),
                s.getSettledAt().toLocalDate(),
                new UserRefDto(s.getFromUser().getId(), s.getFromUser().getName()),
                new UserRefDto(s.getToUser().getId(), s.getToUser().getName()),
                s.getAmount().doubleValue(),
                s.getDescription()
            ))
            .collect(Collectors.toList());
    }
}