package com.competitorintel.platform.service;

import com.competitorintel.platform.domain.enums.CompetitorStatus;
import com.competitorintel.platform.dto.request.CreateCompetitorRequest;
import com.competitorintel.platform.dto.request.UpdateCompetitorRequest;
import com.competitorintel.platform.dto.response.CompetitorResponse;
import com.competitorintel.platform.dto.response.CompetitorSummaryResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompetitorService {
    CompetitorResponse create(CreateCompetitorRequest request);
    CompetitorResponse getById(Long id);
    PageResponse<CompetitorResponse> getAll(String search, CompetitorStatus status, String industry, Pageable pageable);
    List<CompetitorSummaryResponse> getAllActive();
    List<String> getIndustries();
    CompetitorResponse update(Long id, UpdateCompetitorRequest request);
    void delete(Long id);
    void archive(Long id);
    void activate(Long id);
}
