package com.sumukh.debenback.dtos;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateSettlementRequest {
    private Long fromUserId;
    private Long toUserId;
    private Double amount;
    private String description;
    private String date;
}