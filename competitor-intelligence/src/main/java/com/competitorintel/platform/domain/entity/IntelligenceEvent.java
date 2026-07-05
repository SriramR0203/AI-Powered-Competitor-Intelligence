package com.competitorintel.platform.domain.entity;

import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.ProcessingStatus;
import com.competitorintel.platform.domain.enums.SentimentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "intelligence_events", indexes = {
        @Index(name = "idx_events_competitor",   columnList = "competitor_id"),
        @Index(name = "idx_events_source",       columnList = "source_id"),
        @Index(name = "idx_events_category",     columnList = "category"),
        @Index(name = "idx_events_sentiment",    columnList = "sentiment"),
        @Index(name = "idx_events_status",       columnList = "processing_status"),
        @Index(name = "idx_events_published_at", columnList = "published_at"),
        @Index(name = "idx_events_url_hash",     columnList = "url_hash", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"competitor", "source", "tags"})
@EqualsAndHashCode(of = "urlHash", callSuper = false)
public class IntelligenceEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competitor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_event_competitor"))
    private Competitor competitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id",
            foreignKey = @ForeignKey(name = "fk_event_source"))
    private IntelligenceSource source;

    @Column(name = "title", nullable = false, length = 1000)
    private String title;

    @Column(name = "url", length = 2000)
    private String url;

    @Column(name = "url_hash", nullable = false, unique = true, length = 64)
    private String urlHash;

    @Column(name = "raw_content", columnDefinition = "LONGTEXT")
    private String rawContent;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "key_insights", columnDefinition = "TEXT")
    private String keyInsights;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)
    private IntelligenceCategory category = IntelligenceCategory.UNCLASSIFIED;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment", length = 20)
    private SentimentType sentiment = SentimentType.NEUTRAL;

    @Column(name = "sentiment_score")
    private Double sentimentScore;

    @Column(name = "relevance_score")
    private Double relevanceScore;

    @Column(name = "importance_score")
    private Double importanceScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 30)
    private ProcessingStatus processingStatus = ProcessingStatus.RAW;

    @Column(name = "ai_provider", length = 50)
    private String aiProvider;

    @Column(name = "ai_model", length = 100)
    private String aiModel;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "author", length = 255)
    private String author;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "language", length = 10)
    private String language = "en";

    @Column(name = "is_flagged", nullable = false)
    private boolean isFlagged = false;

    @Column(name = "flag_reason", length = 500)
    private String flagReason;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_tags",
            joinColumns        = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                    @Index(name = "idx_event_tags_event", columnList = "event_id"),
                    @Index(name = "idx_event_tags_tag",   columnList = "tag_id")
            })
    private Set<Tag> tags = new HashSet<>();
}
