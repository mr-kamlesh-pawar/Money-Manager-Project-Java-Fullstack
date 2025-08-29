package com.kp.moneyManager.controller;

import com.kp.moneyManager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashBoardData() {
        Map<String, Object> dashBoardData = dashboardService.getDashBoardData();
        return ResponseEntity.ok(dashBoardData);

    }
}
