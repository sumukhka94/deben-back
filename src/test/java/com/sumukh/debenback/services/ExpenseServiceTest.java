package com.sumukh.debenback.services;

import com.sumukh.debenback.dtos.CreateExpenseRequest;
import com.sumukh.debenback.dtos.CreateSettlementRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sumukh.debenback.repositories.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SettlementRepository settlementRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void testValidateExpenseRequest_InvalidTotalAmount() {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setTotalAmount(0.0);
        request.setPayers(Arrays.asList(new CreateExpenseRequest.PayerRequest()));
        request.setSplitters(Arrays.asList(1L));

        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.addExpense(1L, request);
        });
    }

    @Test
    void testValidateExpenseRequest_MismatchedPaidAmount() {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setTotalAmount(100.0);
        
        CreateExpenseRequest.PayerRequest payer = new CreateExpenseRequest.PayerRequest();
        payer.setUserId(1L);
        payer.setAmountPaid(50.0);
        request.setPayers(Arrays.asList(payer));
        request.setSplitters(Arrays.asList(1L));

        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.addExpense(1L, request);
        });
    }

    @Test
    void testValidateSettlementRequest_SameUser() {
        CreateSettlementRequest request = new CreateSettlementRequest();
        request.setFromUserId(1L);
        request.setToUserId(1L);
        request.setAmount(50.0);

        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.recordSettlement(1L, request);
        });
    }
}