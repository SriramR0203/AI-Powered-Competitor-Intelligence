package com.competitorintel.platform.dto.response;

import com.competitorintel.platform.domain.enums.AlertSeverity;
import com.competitorintel.platform.domain.enums.AlertStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertNotificationResponse {
    private Long          id;
    private Long          alertRuleId;
    private String        alertRuleName;
    private Long          eventId;
    private String        eventTitle;
    private String        competitorName;
    private String        title;
    private String        message;
    private AlertSeverity severity;
    private AlertStatus   status;
    private LocalDateTime sentAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime createdAt;
}
