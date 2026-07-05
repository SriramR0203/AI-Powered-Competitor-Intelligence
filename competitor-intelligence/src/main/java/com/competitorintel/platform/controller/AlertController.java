package com.competitorintel.platform.controller;

import com.competitorintel.platform.dto.request.CreateAlertRuleRequest;
import com.competitorintel.platform.dto.response.AlertNotificationResponse;
import com.competitorintel.platform.dto.response.AlertRuleResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Alert rules and notifications")
public class AlertController {

    private final AlertService alertService;

    // ---- Rules ----

    @PostMapping("/rules")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Create an alert rule")
    public ResponseEntity<AlertRuleResponse> createRule(@Valid @RequestBody CreateAlertRuleRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createRule(req));
    }

    @GetMapping("/rules")
    @Operation(summary = "List all alert rules")
    public ResponseEntity<List<AlertRuleResponse>> getRules() {
        return ResponseEntity.ok(alertService.getAllRules());
    }

    @GetMapping("/rules/{id}")
    public ResponseEntity<AlertRuleResponse> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getRuleById(id));
    }

    @PatchMapping("/rules/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> enableRule(@PathVariable Long id) {
        alertService.enableRule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/rules/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> disableRule(@PathVariable Long id) {
        alertService.disableRule(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        alertService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Notifications ----

    @GetMapping("/notifications")
    @Operation(summary = "Get my notifications")
    public ResponseEntity<PageResponse<AlertNotificationResponse>> getNotifications(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(alertService.getNotificationsForUser(
                user.getUsername(), PageRequest.of(page, size)));
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Long> unreadCount(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(alertService.countUnreadForUser(user.getUsername()));
    }

    @PatchMapping("/notifications/{id}/acknowledge")
    public ResponseEntity<Void> acknowledge(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails user) {
        alertService.acknowledge(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/notifications/acknowledge-all")
    public ResponseEntity<Void> acknowledgeAll(@AuthenticationPrincipal UserDetails user) {
        alertService.acknowledgeAll(user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
