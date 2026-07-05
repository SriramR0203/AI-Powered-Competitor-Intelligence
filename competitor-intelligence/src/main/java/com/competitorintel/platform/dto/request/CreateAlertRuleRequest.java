package com.competitorintel.platform.dto.request;

import com.competitorintel.platform.domain.enums.AlertSeverity;
import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.SentimentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAlertRuleRequest {

    @NotBlank(message = "Rule name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    private Long   competitorId;

    private IntelligenceCategory categoryFilter;

    private SentimentType sentimentFilter;

    @Size(max = 1000)
    private String keywordFilter;

    private Double minRelevanceScore;

    private Double minImportanceScore;

    private AlertSeverity severity = AlertSeverity.MEDIUM;

    private boolean notifyEmail  = true;
    private boolean notifyInApp  = true;
    private int     cooldownMinutes = 60;
}
