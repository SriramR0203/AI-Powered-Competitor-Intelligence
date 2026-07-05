package com.competitorintel.platform.dto.response;

import com.competitorintel.platform.domain.enums.ScrapingStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScrapingJobResponse {
    private Long          id;
    private Long          sourceId;
    private String        sourceName;
    private Long          competitorId;
    private String        competitorName;
    private ScrapingStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long          durationMs;
    private int           itemsScraped;
    private int           itemsNew;
    private String        errorMessage;
    private Integer       httpStatusCode;
    private int           retryCount;
    private String        triggeredBy;
    private LocalDateTime createdAt;
}
