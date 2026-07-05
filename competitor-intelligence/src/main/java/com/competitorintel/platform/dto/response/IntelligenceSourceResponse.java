package com.competitorintel.platform.dto.response;

import com.competitorintel.platform.domain.enums.SourceType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IntelligenceSourceResponse {
    private Long id;
    private Long competitorId;
    private String competitorName;
    private String name;
    private String url;
    private SourceType sourceType;
    private int scrapeIntervalHours;
    private String cssSelector;
    private String xpathSelector;
    private boolean requiresJavascript;
    private boolean active;
    private LocalDateTime lastScrapedAt;
    private LocalDateTime nextScrapeAt;
    private int consecutiveFailures;
    private String notes;
    private LocalDateTime createdAt;
}
