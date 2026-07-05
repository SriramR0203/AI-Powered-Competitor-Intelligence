package com.competitorintel.platform.service;

import com.competitorintel.platform.dto.request.CreateAlertRuleRequest;
import com.competitorintel.platform.dto.response.AlertNotificationResponse;
import com.competitorintel.platform.dto.response.AlertRuleResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AlertService {
    AlertRuleResponse createRule(CreateAlertRuleRequest request);
    AlertRuleResponse getRuleById(Long id);
    List<AlertRuleResponse> getAllRules();
    void deleteRule(Long id);
    void enableRule(Long id);
    void disableRule(Long id);

    // Notifications
    PageResponse<AlertNotificationResponse> getNotificationsForUser(String username, Pageable pageable);
    long countUnreadForUser(String username);
    void acknowledge(Long notificationId, String username);
    void acknowledgeAll(String username);

    // Engine
    void evaluateEventAgainstRules(Long eventId);
    void sendPendingEmailNotifications();
}
