package com.competitorintel.platform.service.impl;

import com.competitorintel.platform.domain.entity.Competitor;
import com.competitorintel.platform.domain.enums.CompetitorStatus;
import com.competitorintel.platform.domain.repository.CompetitorRepository;
import com.competitorintel.platform.domain.repository.CompetitorSpecification;
import com.competitorintel.platform.domain.repository.IntelligenceEventRepository;
import com.competitorintel.platform.domain.repository.IntelligenceSourceRepository;
import com.competitorintel.platform.dto.request.CreateCompetitorRequest;
import com.competitorintel.platform.dto.request.UpdateCompetitorRequest;
import com.competitorintel.platform.dto.response.CompetitorResponse;
import com.competitorintel.platform.dto.response.CompetitorSummaryResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.exception.DuplicateResourceException;
import com.competitorintel.platform.exception.ResourceNotFoundException;
import com.competitorintel.platform.mapper.CompetitorMapper;
import com.competitorintel.platform.service.CompetitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitorServiceImpl implements CompetitorService {

    private final CompetitorRepository         competitorRepository;
    private final IntelligenceSourceRepository sourceRepository;
    private final IntelligenceEventRepository  eventRepository;
    private final CompetitorMapper             competitorMapper;

    @Override
    @Transactional
    @CacheEvict(value = "competitors", allEntries = true)
    public CompetitorResponse create(CreateCompetitorRequest request) {
        if (competitorRepository.existsByWebsiteUrl(request.getWebsiteUrl())) {
            throw new DuplicateResourceException("Competitor", "websiteUrl", request.getWebsiteUrl());
        }
        Competitor saved = competitorRepository.save(competitorMapper.toEntity(request));
        log.info("Created competitor id={} name='{}'", saved.getId(), saved.getName());
        return enrich(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "competitor", key = "#id")
    public CompetitorResponse getById(Long id) {
        return enrich(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CompetitorResponse> getAll(String search, CompetitorStatus status,
                                                    String industry, Pageable pageable) {
        Page<Competitor> page = competitorRepository.findAll(
                CompetitorSpecification.withFilters(search, status, industry), pageable);
        return PageResponse.of(page.map(this::enrich));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("competitors")
    public List<CompetitorSummaryResponse> getAllActive() {
        return competitorMapper.toSummaryList(
                competitorRepository.findByStatusOrderByPriorityScoreDesc(CompetitorStatus.ACTIVE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getIndustries() {
        return competitorRepository.findDistinctIndustries();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"competitors", "competitor"}, allEntries = true)
    public CompetitorResponse update(Long id, UpdateCompetitorRequest request) {
        Competitor competitor = findById(id);
        if (request.getWebsiteUrl() != null
                && !request.getWebsiteUrl().equals(competitor.getWebsiteUrl())
                && competitorRepository.existsByWebsiteUrl(request.getWebsiteUrl())) {
            throw new DuplicateResourceException("Competitor", "websiteUrl", request.getWebsiteUrl());
        }
        competitorMapper.updateEntity(request, competitor);
        return enrich(competitorRepository.save(competitor));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"competitors", "competitor"}, allEntries = true)
    public void delete(Long id) {
        competitorRepository.delete(findById(id));
        log.info("Deleted competitor id={}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"competitors", "competitor"}, allEntries = true)
    public void archive(Long id) {
        Competitor c = findById(id);
        c.setStatus(CompetitorStatus.ARCHIVED);
        competitorRepository.save(c);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"competitors", "competitor"}, allEntries = true)
    public void activate(Long id) {
        Competitor c = findById(id);
        c.setStatus(CompetitorStatus.ACTIVE);
        competitorRepository.save(c);
    }

    private Competitor findById(Long id) {
        return competitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competitor", id));
    }

    private CompetitorResponse enrich(Competitor c) {
        CompetitorResponse r = competitorMapper.toResponse(c);
        r.setActiveSourceCount(sourceRepository.countActiveByCompetitorId(c.getId()));
        r.setRecentEventCount(eventRepository.countRecentByCompetitor(
                c.getId(), LocalDateTime.now().minusDays(30)));
        return r;
    }
}
