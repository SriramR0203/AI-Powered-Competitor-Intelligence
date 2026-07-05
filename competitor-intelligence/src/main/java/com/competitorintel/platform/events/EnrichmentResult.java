package com.competitorintel.platform.events;

import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.SentimentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrichmentResult {
    private String               aiSummary;
    private IntelligenceCategory category;
    private SentimentType        sentiment;
    private Double               sentimentScore;
    private Double               relevanceScore;
    private Double               importanceScore;
    private String               keyInsights;
    @Builder.Default
    private List<String>         tags = new ArrayList<>();
    private boolean              parsed;
    private String               rawResponse;
}
