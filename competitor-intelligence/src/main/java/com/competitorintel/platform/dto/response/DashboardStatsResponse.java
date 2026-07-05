package com.competitorintel.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    // KPIs
    private long totalCompetitors;
    private long activeCompetitors;
    private long totalSources;
    private long activeSources;
    private long eventsLast30Days;
    private long eventsLast7Days;
    private long enrichedEvents;
    private long pendingAlerts;

    // Charts
    private Map<String, Long>         eventsByCategory;
    private Map<String, Long>         eventsBySentiment;
    private List<Map<String, Object>> eventsTrend;        // [{date, count}]
    private List<Map<String, Object>> topCompetitors;     // [{name, eventCount}]
    private Map<String, Long>         scrapeSuccessRate;
}
