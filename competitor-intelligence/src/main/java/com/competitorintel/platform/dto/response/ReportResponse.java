package com.competitorintel.platform.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportResponse {
    private Long          id;
    private String        name;
    private String        description;
    private Long          competitorId;
    private String        competitorName;
    private String        reportType;
    private String        format;
    private Long          fileSizeBytes;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Integer       rowCount;
    private LocalDateTime generatedAt;
    private int           downloadCount;
    private LocalDateTime createdAt;
    private String        createdBy;
}
