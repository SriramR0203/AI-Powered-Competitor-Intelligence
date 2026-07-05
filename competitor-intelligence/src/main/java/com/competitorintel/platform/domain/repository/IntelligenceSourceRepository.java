package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.IntelligenceSource;
import com.competitorintel.platform.domain.enums.SourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IntelligenceSourceRepository extends JpaRepository<IntelligenceSource, Long> {

    List<IntelligenceSource> findByCompetitorId(Long competitorId);

    Page<IntelligenceSource> findByCompetitorId(Long competitorId, Pageable pageable);

    List<IntelligenceSource> findBySourceType(SourceType sourceType);

    List<IntelligenceSource> findByIsActiveTrueOrderByNextScrapeAt();

    @Query("""
           SELECT s FROM IntelligenceSource s
           WHERE s.isActive = true
           AND (s.nextScrapeAt IS NULL OR s.nextScrapeAt <= :now)
           ORDER BY s.nextScrapeAt ASC NULLS FIRST
           """)
    List<IntelligenceSource> findSourcesDueForScraping(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE IntelligenceSource s SET s.consecutiveFailures = s.consecutiveFailures + 1 WHERE s.id = :id")
    void incrementConsecutiveFailures(@Param("id") Long id);

    @Modifying
    @Query("UPDATE IntelligenceSource s SET s.consecutiveFailures = 0, s.lastScrapedAt = :now WHERE s.id = :id")
    void resetConsecutiveFailures(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE IntelligenceSource s SET s.isActive = false WHERE s.consecutiveFailures >= s.maxFailuresBeforeDisable")
    void disableFailingSources();

    @Query("SELECT COUNT(s) FROM IntelligenceSource s WHERE s.competitor.id = :cid AND s.isActive = true")
    long countActiveByCompetitorId(@Param("cid") Long competitorId);
}
