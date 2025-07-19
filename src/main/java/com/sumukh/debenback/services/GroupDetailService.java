package com.sumukh.debenback.services;

import com.sumukh.debenback.dtos.*;
import com.sumukh.debenback.entities.*;
import com.sumukh.debenback.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupDetailService {
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpensePayerRepository expensePayerRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final SettlementRepository settlementRepository;

    public GroupDetailDto getGroupDetail(Long groupId) {
        Group group = groupRepository.findByIdWithMembers(groupId).orElseThrow(() -> new EntityNotFoundException("Group with id " + groupId + " not found"));

        List<MemberBalanceDto> members = buildMemberBalances(groupId);

        List<Expense> expenses = expenseRepository.findByGroupIdOrderByPaidAtDesc(groupId);
        List<ExpenseDto> expenseDtos = buildExpenseDtos(expenses);

        List<SettlementDto> settlements = mapSettlements(group);

        return new GroupDetailDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                members,
                expenseDtos,
                settlements
        );
    }
    private List<MemberBalanceDto> buildMemberBalances(Long groupId) {
        List<Object[]> rows = groupRepository.computeMemberBalances(groupId);
        return rows.stream().map(r -> {
            Long userId = ((Number) r[0]).longValue();
            String name = (String) r[1];
            double paid = ((Number) r[2]).doubleValue();
            double share = ((Number) r[3]).doubleValue();
            double balance = paid - share;
            return new MemberBalanceDto(userId, name, "/placeholder.svg?height=40&width=40", balance);
        }).toList();
    }
    private List<ExpenseDto> buildExpenseDtos(List<Expense> expenses) {
        if (expenses.isEmpty()) return List.of();

        List<Long> ids = expenses.stream().map(Expense::getId).toList();

        // batch load payers & splits
        List<ExpensePayer> payers = expensePayerRepository.findByExpenseIds(ids);
        List<ExpenseSplit> splits = expenseSplitRepository.findSplitsByExpenseIds(ids);

        // index them
        Map<Long, List<ExpensePayer>> payersByExpense =
                payers.stream().collect(Collectors.groupingBy(ep -> ep.getExpense().getId()));

        Map<Long, List<ExpenseSplit>> splitsByExpense =
                splits.stream().collect(Collectors.groupingBy(es -> es.getExpense().getId()));

        DateTimeFormatter dateFmt = DateTimeFormatter.ISO_LOCAL_DATE;

        return expenses.stream().map(e -> {
            List<PaidByDto> paidBy = payersByExpense
                    .getOrDefault(e.getId(), List.of())
                    .stream()
                    .map(ep -> new PaidByDto(ep.getUser().getId(), ep.getAmountPaid()))
                    .toList();

            // For each split user, find how much they paid (if any) and compute owes
            Map<Long, Double> paidMap = paidBy.stream()
                    .collect(Collectors.toMap(PaidByDto::getUserId, PaidByDto::getAmount));

            List<ExpenseShareDto> splitAmong = splitsByExpense
                    .getOrDefault(e.getId(), List.of())
                    .stream()
                    .map(es -> {
                        Long userId = es.getUser().getId();
                        double share = es.getAmountOwed();
                        double paid = paidMap.getOrDefault(userId, 0.0);
                        double owes = Math.max(share - paid, 0.0);
                        return new ExpenseShareDto(userId, share, paid, owes);
                    })
                    .toList();

            return new ExpenseDto(
                    e.getId(),
                    e.getTitle(),                      // or e.getDescription() if no title field
                    e.getDescription(),
                    e.getPaidAt().toLocalDate().format(dateFmt),
                    e.getTotalAmount(),
                    paidBy,
                    splitAmong
            );
        }).toList();
    }
    private List<SettlementDto> mapSettlements(Group group) {
        List<Settlement> settlements =
                settlementRepository.findByGroupIdOrderBySettledAtDesc(group.getId());

        return settlements.stream()
                .map(this::toSettlementDto)
                .collect(Collectors.toList());
    }
    private SettlementDto toSettlementDto(Settlement s) {
        return new SettlementDto(
                s.getId(),
                s.getSettledAt().toLocalDate(),
                new UserRefDto(s.getFromUser().getId(), s.getFromUser().getName()),
                new UserRefDto(s.getToUser().getId(), s.getToUser().getName()),
                s.getAmount().doubleValue(),
                s.getDescription()
        );
    }
}
