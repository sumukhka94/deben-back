package com.sumukh.debenback.dtos;

import lombok.Data;
import java.util.List;

@Data
public class CreateExpenseRequest {
    private String title;
    private String description;
    private Double totalAmount;
    private List<PayerRequest> payers;
    private List<Long> splitters;
    private String splitType;
    private List<CustomSplitRequest> customSplits;
    private List<CustomAmountRequest> customAmounts;

    @Data
    public static class PayerRequest {
        private Long userId;
        private Double amountPaid;
    }

    @Data
    public static class CustomSplitRequest {
        private Long userId;
        private Double percentage;
    }

    @Data
    public static class CustomAmountRequest {
        private Long userId;
        private Double amount;
    }
}
