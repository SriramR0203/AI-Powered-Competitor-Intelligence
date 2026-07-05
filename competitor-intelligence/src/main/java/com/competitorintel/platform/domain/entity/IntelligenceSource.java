package com.competitorintel.platform.domain.entity;

import com.competitorintel.platform.domain.enums.SourceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "intelligence_sources", indexes = {
        @Index(name = "idx_sources_competitor",  columnList = "competitor_id"),
        @Index(name = "idx_sources_type",        columnList = "source_type"),
        @Index(name = "idx_sources_active",      columnList = "is_active"),
        @Index(name = "idx_sources_next_scrape", columnList = "next_scrape_at")
})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"competitor", "scrapingJobs"})
@EqualsAndHashCode(callSuper = false)
public class IntelligenceSource extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competitor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_source_competitor"))
    private Competitor competitor;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private SourceType sourceType;

    @Column(name = "scrape_interval_hours", nullable = false)
    private int scrapeIntervalHours = 6;

    @Column(name = "css_selector", length = 500)
    private String cssSelector;

    @Column(name = "xpath_selector", length = 500)
    private String xpathSelector;

    @Column(name = "requires_javascript", nullable = false)
    private boolean requiresJavascript = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "last_scraped_at")
    private LocalDateTime lastScrapedAt;

    @Column(name = "next_scrape_at")
    private LocalDateTime nextScrapeAt;

    @Column(name = "consecutive_failures", nullable = false)
    private int consecutiveFailures = 0;

    @Column(name = "max_failures_before_disable", nullable = false)
    private int maxFailuresBeforeDisable = 5;

    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @Column(name = "http_headers", columnDefinition = "TEXT")
    private String httpHeaders;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScrapingJob> scrapingJobs = new ArrayList<>();
}
