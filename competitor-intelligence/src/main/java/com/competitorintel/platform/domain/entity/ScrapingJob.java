package com.competitorintel.platform.domain.entity;

import com.competitorintel.platform.domain.enums.ScrapingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scraping_jobs", indexes = {
        @Index(name = "idx_scraping_jobs_source",     columnList = "source_id"),
        @Index(name = "idx_scraping_jobs_status",     columnList = "status"),
        @Index(name = "idx_scraping_jobs_started_at", columnList = "started_at"),
        @Index(name = "idx_scraping_jobs_competitor", columnList = "competitor_id")
})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "source")
@EqualsAndHashCode(callSuper = false)
public class ScrapingJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_scraping_job_source"))
    private IntelligenceSource source;

    @Column(name = "competitor_id", nullable = false)
    private Long competitorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ScrapingStatus status = ScrapingStatus.PENDING;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "items_scraped", nullable = false)
    private int itemsScraped = 0;

    @Column(name = "items_new", nullable = false)
    private int itemsNew = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "http_status_code")
    private Integer httpStatusCode;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "content_size_bytes")
    private Long contentSizeBytes;

    @Column(name = "triggered_by", length = 50)
    private String triggeredBy = "SCHEDULER";
}
