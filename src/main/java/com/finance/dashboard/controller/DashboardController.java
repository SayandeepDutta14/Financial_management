package com.finance.dashboard.controller;

import com.finance.dashboard.dto.DashboardSummaryResponse;
import com.finance.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard/summary
     * Returns: total income, total expenses, net balance,
     *          category breakdowns, monthly trends, recent activity
     * Accessible by: VIEWER, ANALYST, ADMIN
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
