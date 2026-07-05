package com.competitorintel.platform.dto.response;

import com.competitorintel.platform.domain.enums.CompetitorStatus;
import lombok.Data;

@Data
public class CompetitorSummaryResponse {
    private Long id;
    private String name;
    private String websiteUrl;
    private String industry;
    private CompetitorStatus status;
    private int priorityScore;
    private String logoUrl;
}
