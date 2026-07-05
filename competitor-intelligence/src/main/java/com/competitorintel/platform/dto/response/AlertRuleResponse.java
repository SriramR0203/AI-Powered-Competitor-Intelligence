package com.competitorintel.platform.dto.response;

import com.competitorintel.platform.domain.enums.AlertSeverity;
import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.SentimentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertRuleResponse {
    private Long                 id;
    private String               name;
    private String               description;
    private Long                 competitorId;
    private String               competitorName;
    private IntelligenceCategory categoryFilter;
    private SentimentType        sentimentFilter;
    private String               keywordFilter;
    private Double               minRelevanceScore;
    private Double               minImportanceScore;
    private AlertSeverity        severity;
    private boolean              active;
    private boolean              notifyEmail;
    private boolean              notifyInApp;
    private int                  cooldownMinutes;
    private LocalDateTime        createdAt;
}
