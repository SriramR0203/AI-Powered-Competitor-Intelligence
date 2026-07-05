package com.competitorintel.platform.service;

import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.dto.response.ReportResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    void exportEventsCsv(Long competitorId, int daysBack, String username, HttpServletResponse response) throws Exception;
    void exportEventsPdf(Long competitorId, int daysBack, String username, HttpServletResponse response) throws Exception;
    PageResponse<ReportResponse> getMyReports(String username, Pageable pageable);
    void deleteReport(Long id, String username);
}
