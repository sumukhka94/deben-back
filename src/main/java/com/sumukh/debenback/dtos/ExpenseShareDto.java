package com.sumukh.debenback.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExpenseShareDto {
    private Long userId;
    private double share;
    private double paid;
    private double owes;
}
