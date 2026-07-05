package com.competitorintel.platform.domain.entity;

import com.competitorintel.platform.domain.enums.AlertSeverity;
import com.competitorintel.platform.domain.enums.AlertStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_notifications", indexes = {
        @Index(name = "idx_notifications_user",    columnList = "user_id"),
        @Index(name = "idx_notifications_rule",    columnList = "alert_rule_id"),
        @Index(name = "idx_notifications_event",   columnList = "intelligence_event_id"),
        @Index(name = "idx_notifications_status",  columnList = "status"),
        @Index(name = "idx_notifications_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"alertRule", "user", "intelligenceEvent"})
@EqualsAndHashCode(callSuper = false)
public class AlertNotification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alert_rule_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_notification_rule"))
    private AlertRule alertRule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_notification_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intelligence_event_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_notification_event"))
    private IntelligenceEvent intelligenceEvent;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private AlertSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AlertStatus status = AlertStatus.PENDING;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "email_recipient", length = 255)
    private String emailRecipient;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;
}
