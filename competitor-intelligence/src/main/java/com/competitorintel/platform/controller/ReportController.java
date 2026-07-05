package com.competitorintel.platform.controller;

import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.dto.response.ReportResponse;
import com.competitorintel.platform.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Export events to CSV and PDF")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/export/csv")
    @Operation(summary = "Export intelligence events to CSV")
    public void exportCsv(
            @RequestParam(required = false) Long competitorId,
            @RequestParam(defaultValue = "30") int daysBack,
            @AuthenticationPrincipal UserDetails user,
            HttpServletResponse response) throws Exception {
        reportService.exportEventsCsv(competitorId, daysBack, user.getUsername(), response);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Export intelligence events to PDF")
    public void exportPdf(
            @RequestParam(required = false) Long competitorId,
            @RequestParam(defaultValue = "30") int daysBack,
            @AuthenticationPrincipal UserDetails user,
            HttpServletResponse response) throws Exception {
        reportService.exportEventsPdf(competitorId, daysBack, user.getUsername(), response);
    }

    @GetMapping
    @Operation(summary = "List my generated reports")
    public ResponseEntity<PageResponse<ReportResponse>> getMyReports(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reportService.getMyReports(
                user.getUsername(),
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails user) {
        reportService.deleteReport(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
