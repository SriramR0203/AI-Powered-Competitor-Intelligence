package com.competitorintel.platform.service.impl;

import com.competitorintel.platform.domain.entity.Competitor;
import com.competitorintel.platform.domain.entity.IntelligenceSource;
import com.competitorintel.platform.domain.repository.CompetitorRepository;
import com.competitorintel.platform.domain.repository.IntelligenceSourceRepository;
import com.competitorintel.platform.dto.request.CreateSourceRequest;
import com.competitorintel.platform.dto.request.UpdateSourceRequest;
import com.competitorintel.platform.dto.response.IntelligenceSourceResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.exception.ResourceNotFoundException;
import com.competitorintel.platform.mapper.IntelligenceSourceMapper;
import com.competitorintel.platform.service.IntelligenceSourceService;
import com.competitorintel.platform.service.ScrapingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntelligenceSourceServiceImpl implements IntelligenceSourceService {

    private final IntelligenceSourceRepository sourceRepository;
    private final CompetitorRepository         competitorRepository;
    private final IntelligenceSourceMapper     sourceMapper;
    private final ApplicationContext           ctx;

    @Override
    @Transactional
    public IntelligenceSourceResponse create(CreateSourceRequest request) {
        Competitor competitor = competitorRepository.findById(request.getCompetitorId())
                .orElseThrow(() -> new ResourceNotFoundException("Competitor", request.getCompetitorId()));

        IntelligenceSource source = sourceMapper.toEntity(request);
        source.setCompetitor(competitor);
        source.setNextScrapeAt(LocalDateTime.now());

        IntelligenceSource saved = sourceRepository.save(source);
        log.info("Created source id={} for competitor '{}'", saved.getId(), competitor.getName());
        return sourceMapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public IntelligenceSourceResponse getById(Long id) {
        return sourceMapper.toResponse(findById(id));
    }

    @Override @Transactional(readOnly = true)
    public List<IntelligenceSourceResponse> getByCompetitorId(Long competitorId) {
        return sourceMapper.toResponseList(sourceRepository.findByCompetitorId(competitorId));
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<IntelligenceSourceResponse> getAll(Long competitorId, Pageable pageable) {
        Page<IntelligenceSource> page = (competitorId != null)
                ? sourceRepository.findByCompetitorId(competitorId, pageable)
                : sourceRepository.findAll(pageable);
        return PageResponse.of(page.map(sourceMapper::toResponse));
    }

    @Override @Transactional
    public IntelligenceSourceResponse update(Long id, UpdateSourceRequest request) {
        IntelligenceSource source = findById(id);
        sourceMapper.updateEntity(request, source);
        if (request.getScrapeIntervalHours() != null) {
            source.setNextScrapeAt(LocalDateTime.now().plusHours(request.getScrapeIntervalHours()));
        }
        return sourceMapper.toResponse(sourceRepository.save(source));
    }

    @Override @Transactional
    public void enable(Long id) {
        IntelligenceSource s = findById(id);
        s.setActive(true);
        s.setConsecutiveFailures(0);
        s.setNextScrapeAt(LocalDateTime.now());
        sourceRepository.save(s);
    }

    @Override @Transactional
    public void disable(Long id) {
        IntelligenceSource s = findById(id);
        s.setActive(false);
        sourceRepository.save(s);
    }

    @Override @Transactional
    public void delete(Long id) {
        sourceRepository.delete(findById(id));
    }

    @Override
    public void triggerManualScrape(Long id) {
        findById(id); // validates existence
        ctx.getBean(ScrapingService.class).scrapeSourceAsync(id);
        log.info("Manual scrape triggered for source id={}", id);
    }

    private IntelligenceSource findById(Long id) {
        return sourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IntelligenceSource", id));
    }
}
