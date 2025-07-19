package com.sumukh.debenback.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SettlementDto {
    private Long id;
    private LocalDate date;
    private UserRefDto from;
    private UserRefDto to;
    private double amount;
    private String description;
}

