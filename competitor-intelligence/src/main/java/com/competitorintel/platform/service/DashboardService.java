package com.competitorintel.platform.service;

import com.competitorintel.platform.dto.response.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getStats(int daysBack);
}
