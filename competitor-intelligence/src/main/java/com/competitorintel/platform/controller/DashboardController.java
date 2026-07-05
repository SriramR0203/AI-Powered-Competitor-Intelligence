package com.competitorintel.platform.controller;

import com.competitorintel.platform.dto.response.DashboardStatsResponse;
import com.competitorintel.platform.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "KPIs and chart data for the dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get KPI statistics and chart data")
    public ResponseEntity<DashboardStatsResponse> getStats(
            @RequestParam(defaultValue = "30") int daysBack) {
        return ResponseEntity.ok(dashboardService.getStats(daysBack));
    }
}
