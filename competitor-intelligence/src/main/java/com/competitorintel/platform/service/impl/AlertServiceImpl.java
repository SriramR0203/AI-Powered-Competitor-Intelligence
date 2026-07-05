package com.competitorintel.platform.service.impl;

import com.competitorintel.platform.domain.entity.*;
import com.competitorintel.platform.domain.enums.AlertStatus;
import com.competitorintel.platform.domain.repository.*;
import com.competitorintel.platform.dto.request.CreateAlertRuleRequest;
import com.competitorintel.platform.dto.response.AlertNotificationResponse;
import com.competitorintel.platform.dto.response.AlertRuleResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.exception.ResourceNotFoundException;
import com.competitorintel.platform.mapper.AlertNotificationMapper;
import com.competitorintel.platform.mapper.AlertRuleMapper;
import com.competitorintel.platform.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRuleRepository         ruleRepository;
    private final AlertNotificationRepository notificationRepository;
    private final AlertSubscriptionRepository subscriptionRepository;
    private final IntelligenceEventRepository eventRepository;
    private final CompetitorRepository        competitorRepository;
    private final UserRepository              userRepository;
    private final AlertRuleMapper             ruleMapper;
    private final AlertNotificationMapper     notificationMapper;
    private final JavaMailSender              mailSender;

    // ------------------------------------------------------------------ //
    //  Rules
    // ------------------------------------------------------------------ //

    @Override @Transactional
    public AlertRuleResponse createRule(CreateAlertRuleRequest request) {
        AlertRule rule = ruleMapper.toEntity(request);
        if (request.getCompetitorId() != null) {
            rule.setCompetitor(competitorRepository.findById(request.getCompetitorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Competitor", request.getCompetitorId())));
        }
        return ruleMapper.toResponse(ruleRepository.save(rule));
    }

    @Override @Transactional(readOnly = true)
    public AlertRuleResponse getRuleById(Long id) {
        return ruleMapper.toResponse(findRule(id));
    }

    @Override @Transactional(readOnly = true)
    public List<AlertRuleResponse> getAllRules() {
        return ruleMapper.toResponseList(ruleRepository.findAll());
    }

    @Override @Transactional
    public void deleteRule(Long id) { ruleRepository.delete(findRule(id)); }

    @Override @Transactional
    public void enableRule(Long id) { AlertRule r = findRule(id); r.setActive(true); ruleRepository.save(r); }

    @Override @Transactional
    public void disableRule(Long id) { AlertRule r = findRule(id); r.setActive(false); ruleRepository.save(r); }

    // ------------------------------------------------------------------ //
    //  Notifications
    // ------------------------------------------------------------------ //

    @Override @Transactional(readOnly = true)
    public PageResponse<AlertNotificationResponse> getNotificationsForUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return PageResponse.of(
                notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                        .map(notificationMapper::toResponse));
    }

    @Override @Transactional(readOnly = true)
    public long countUnreadForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return notificationRepository.countByUserIdAndStatus(user.getId(), AlertStatus.PENDING);
    }

    @Override @Transactional
    public void acknowledge(Long notificationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        notificationRepository.acknowledgeNotification(notificationId, user.getId(), LocalDateTime.now());
    }

    @Override @Transactional
    public void acknowledgeAll(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        notificationRepository.acknowledgeAllForUser(user.getId(), LocalDateTime.now());
    }

    // ------------------------------------------------------------------ //
    //  Alert engine
    // ------------------------------------------------------------------ //

    @Override @Transactional
    public void evaluateEventAgainstRules(Long eventId) {
        IntelligenceEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("IntelligenceEvent", eventId));

        List<AlertRule> rules = ruleRepository.findActiveRulesForCompetitor(
                event.getCompetitor().getId());

        for (AlertRule rule : rules) {
            if (!matchesRule(rule, event)) continue;

            // Check cooldown — skip if already fired recently
            long recentCount = notificationRepository.countRecentByRule(
                    rule.getId(), LocalDateTime.now().minusMinutes(rule.getCooldownMinutes()));
            if (recentCount > 0) {
                log.debug("Skipping rule {} for event {} due to cooldown", rule.getId(), eventId);
                continue;
            }

            // Notify all subscribers
            List<AlertSubscription> subs = subscriptionRepository.findByAlertRuleId(rule.getId());
            for (AlertSubscription sub : subs) {
                createNotification(rule, sub.getUser(), event);
            }
        }
    }

    @Override @Async
    public void sendPendingEmailNotifications() {
        List<AlertNotification> pending = notificationRepository.findPendingEmailNotifications();
        for (AlertNotification n : pending) {
            try {
                sendEmail(n);
                n.setStatus(AlertStatus.SENT);
                n.setSentAt(LocalDateTime.now());
                notificationRepository.save(n);
            } catch (Exception e) {
                log.error("Failed to send notification email id={}: {}", n.getId(), e.getMessage());
                n.setStatus(AlertStatus.FAILED);
                n.setErrorMessage(e.getMessage());
                notificationRepository.save(n);
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Helpers
    // ------------------------------------------------------------------ //

    private boolean matchesRule(AlertRule rule, IntelligenceEvent event) {
        if (rule.getCategoryFilter() != null
                && rule.getCategoryFilter() != event.getCategory()) return false;
        if (rule.getSentimentFilter() != null
                && rule.getSentimentFilter() != event.getSentiment()) return false;
        if (rule.getMinImportanceScore() != null && event.getImportanceScore() != null
                && event.getImportanceScore() < rule.getMinImportanceScore()) return false;
        if (rule.getMinRelevanceScore() != null && event.getRelevanceScore() != null
                && event.getRelevanceScore() < rule.getMinRelevanceScore()) return false;
        if (StringUtils.hasText(rule.getKeywordFilter())) {
            String kw = rule.getKeywordFilter().toLowerCase();
            String title = event.getTitle() != null ? event.getTitle().toLowerCase() : "";
            if (!title.contains(kw)) return false;
        }
        return true;
    }

    private void createNotification(AlertRule rule, User user, IntelligenceEvent event) {
        AlertNotification n = new AlertNotification();
        n.setAlertRule(rule);
        n.setUser(user);
        n.setIntelligenceEvent(event);
        n.setSeverity(rule.getSeverity());
        n.setStatus(AlertStatus.PENDING);
        n.setTitle("Alert: " + rule.getName());
        n.setMessage(String.format("Competitor '%s' — %s: %s",
                event.getCompetitor().getName(), event.getCategory(), event.getTitle()));
        if (StringUtils.hasText(user.getEmail())) n.setEmailRecipient(user.getEmail());
        notificationRepository.save(n);
    }

    private void sendEmail(AlertNotification n) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(n.getEmailRecipient());
        msg.setSubject("[Competitor Intel Alert] " + n.getTitle());
        msg.setText(n.getMessage());
        mailSender.send(msg);
    }

    private AlertRule findRule(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertRule", id));
    }
}
