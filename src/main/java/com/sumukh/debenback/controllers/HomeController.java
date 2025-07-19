package com.sumukh.debenback.controllers;

import com.sumukh.debenback.dtos.GroupSummaryDto;
import com.sumukh.debenback.services.HomeService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/home")
    public List<GroupSummaryDto> getHomeData() {
        return homeService.getHomeDataForUser(1L);
    }
}
