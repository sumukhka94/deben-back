package com.sumukh.debenback.controllers;

import com.sumukh.debenback.dtos.GroupDetailDto;
import com.sumukh.debenback.services.GroupDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:5173")
public class GroupDetailController {

    private final GroupDetailService groupDetailService;

    @GetMapping("/{groupId}")
    public GroupDetailDto getGroup(@PathVariable Long groupId) {
        return groupDetailService.getGroupDetail(groupId);
    }
}
