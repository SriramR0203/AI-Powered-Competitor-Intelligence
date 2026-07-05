package com.competitorintel.platform.dto.response;

import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.ProcessingStatus;
import com.competitorintel.platform.domain.enums.SentimentType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class IntelligenceEventResponse {
    private Long                id;
    private Long                competitorId;
    private String              competitorName;
    private Long                sourceId;
    private String              sourceName;
    private String              title;
    private String              url;
    private String              summary;
    private String              aiSummary;
    private String              keyInsights;
    private IntelligenceCategory category;
    private SentimentType       sentiment;
    private Double              sentimentScore;
    private Double              relevanceScore;
    private Double              importanceScore;
    private ProcessingStatus    processingStatus;
    private String              aiProvider;
    private String              aiModel;
    private LocalDateTime       publishedAt;
    private LocalDateTime       processedAt;
    private String              author;
    private String              imageUrl;
    private String              language;
    private boolean             flagged;
    private String              flagReason;
    private Set<String>         tags;
    private LocalDateTime       createdAt;
}
