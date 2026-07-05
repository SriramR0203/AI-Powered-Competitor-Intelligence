package com.competitorintel.platform.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alert_subscriptions",
        uniqueConstraints = @UniqueConstraint(name = "uq_subscription_user_rule",
                columnNames = {"user_id", "alert_rule_id"}),
        indexes = {
                @Index(name = "idx_subscriptions_user", columnList = "user_id"),
                @Index(name = "idx_subscriptions_rule", columnList = "alert_rule_id")
        })
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"user", "alertRule"}, callSuper = false)
public class AlertSubscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_subscription_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alert_rule_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_subscription_rule"))
    private AlertRule alertRule;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = true;

    @Column(name = "in_app_enabled", nullable = false)
    private boolean inAppEnabled = true;
}
