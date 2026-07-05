package com.competitorintel.platform.service;

import com.competitorintel.platform.domain.entity.Competitor;
import com.competitorintel.platform.domain.enums.CompetitorStatus;
import com.competitorintel.platform.domain.repository.CompetitorRepository;
import com.competitorintel.platform.domain.repository.IntelligenceEventRepository;
import com.competitorintel.platform.domain.repository.IntelligenceSourceRepository;
import com.competitorintel.platform.dto.request.CreateCompetitorRequest;
import com.competitorintel.platform.dto.response.CompetitorResponse;
import com.competitorintel.platform.exception.DuplicateResourceException;
import com.competitorintel.platform.exception.ResourceNotFoundException;
import com.competitorintel.platform.mapper.CompetitorMapper;
import com.competitorintel.platform.service.impl.CompetitorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitorServiceTest {

    @Mock CompetitorRepository         competitorRepository;
    @Mock IntelligenceSourceRepository sourceRepository;
    @Mock IntelligenceEventRepository  eventRepository;
    @Mock CompetitorMapper             competitorMapper;

    @InjectMocks CompetitorServiceImpl competitorService;

    @Test
    void create_whenWebsiteUrlExists_throwsDuplicate() {
        CreateCompetitorRequest req = new CreateCompetitorRequest();
        req.setWebsiteUrl("https://duplicate.com");
        req.setName("Duplicate Corp");

        when(competitorRepository.existsByWebsiteUrl("https://duplicate.com")).thenReturn(true);

        assertThatThrownBy(() -> competitorService.create(req))
                .isInstanceOf(DuplicateResourceException.class);

        verify(competitorRepository, never()).save(any());
    }

    @Test
    void create_happyPath_savesAndReturnsResponse() {
        CreateCompetitorRequest req = new CreateCompetitorRequest();
        req.setWebsiteUrl("https://new.com");
        req.setName("New Corp");

        Competitor entity = new Competitor();
        entity.setWebsiteUrl("https://new.com");
        entity.setName("New Corp");
        entity.setStatus(CompetitorStatus.ACTIVE);

        Competitor saved = new Competitor();
        saved.setId(1L);
        saved.setWebsiteUrl("https://new.com");
        saved.setName("New Corp");
        saved.setStatus(CompetitorStatus.ACTIVE);

        CompetitorResponse resp = new CompetitorResponse();
        resp.setId(1L);
        resp.setName("New Corp");

        when(competitorRepository.existsByWebsiteUrl(any())).thenReturn(false);
        when(competitorMapper.toEntity(req)).thenReturn(entity);
        when(competitorRepository.save(entity)).thenReturn(saved);
        when(competitorMapper.toResponse(saved)).thenReturn(resp);
        when(sourceRepository.countActiveByCompetitorId(1L)).thenReturn(0L);
        when(eventRepository.countRecentByCompetitor(eq(1L), any())).thenReturn(0L);

        CompetitorResponse result = competitorService.create(req);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("New Corp");
        verify(competitorRepository).save(entity);
    }

    @Test
    void getById_notFound_throwsResourceNotFound() {
        when(competitorRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> competitorService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
