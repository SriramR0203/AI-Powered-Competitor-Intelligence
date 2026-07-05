package com.competitorintel.platform.dto.response;

import com.competitorintel.platform.domain.enums.CompetitorStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompetitorResponse {
    private Long id;
    private String name;
    private String websiteUrl;
    private String description;
    private String industry;
    private String headquarters;
    private String employeeCountRange;
    private Integer foundedYear;
    private String linkedinUrl;
    private String twitterHandle;
    private String logoUrl;
    private CompetitorStatus status;
    private int priorityScore;
    private String notes;
    private long activeSourceCount;
    private long recentEventCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
