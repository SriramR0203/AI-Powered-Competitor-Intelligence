package com.competitorintel.platform.domain.entity;

import com.competitorintel.platform.domain.enums.AlertSeverity;
import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.SentimentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alert_rules", indexes = {
        @Index(name = "idx_alert_rules_competitor", columnList = "competitor_id"),
        @Index(name = "idx_alert_rules_active",     columnList = "is_active"),
        @Index(name = "idx_alert_rules_severity",   columnList = "severity")
})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"competitor", "notifications"})
@EqualsAndHashCode(callSuper = false)
public class AlertRule extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_id",
            foreignKey = @ForeignKey(name = "fk_alert_rule_competitor"))
    private Competitor competitor;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_filter", length = 50)
    private IntelligenceCategory categoryFilter;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment_filter", length = 20)
    private SentimentType sentimentFilter;

    @Column(name = "keyword_filter", length = 1000)
    private String keywordFilter;

    @Column(name = "min_relevance_score")
    private Double minRelevanceScore;

    @Column(name = "min_importance_score")
    private Double minImportanceScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private AlertSeverity severity = AlertSeverity.MEDIUM;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "notify_email", nullable = false)
    private boolean notifyEmail = true;

    @Column(name = "notify_in_app", nullable = false)
    private boolean notifyInApp = true;

    @Column(name = "cooldown_minutes", nullable = false)
    private int cooldownMinutes = 60;

    @OneToMany(mappedBy = "alertRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlertNotification> notifications = new ArrayList<>();
}
