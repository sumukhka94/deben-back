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
public class GroupSummaryDto {
    private Long id;
    private String name;
    private String description;
    private List<MembersDto> members;
    private double totalExpenses;
    private int expenseCount;
}
