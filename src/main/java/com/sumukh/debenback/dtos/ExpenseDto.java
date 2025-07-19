package com.sumukh.debenback.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExpenseDto {
    private Long id;
    private String title;
    private String description;
    private String date;
    private Double totalAmount;
    List<PaidByDto> paidBy;
    List<ExpenseShareDto> splitAmong;
}
