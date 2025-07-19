package com.sumukh.debenback.dtos;

import lombok.Data;
import java.util.List;

@Data
public class CreateGroupRequest {
    private String name;
    private String description;
    private Long createdBy;
    private List<Long> memberIds;
}