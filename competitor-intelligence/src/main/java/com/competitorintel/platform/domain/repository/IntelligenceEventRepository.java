package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.IntelligenceEvent;
import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.ProcessingStatus;
import com.competitorintel.platform.domain.enums.SentimentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IntelligenceEventRepository
        extends JpaRepository<IntelligenceEvent, Long>, JpaSpecificationExecutor<IntelligenceEvent> {

    Optional<IntelligenceEvent> findByUrlHash(String urlHash);

    boolean existsByUrlHash(String urlHash);

    Page<IntelligenceEvent> findByCompetitorId(Long competitorId, Pageable pageable);

    Page<IntelligenceEvent> findByCompetitorIdAndCategory(Long competitorId,
                                                           IntelligenceCategory category,
                                                           Pageable pageable);

    @Query("SELECT e FROM IntelligenceEvent e WHERE e.processingStatus = :status ORDER BY e.createdAt ASC")
    List<IntelligenceEvent> findPendingEnrichment(@Param("status") ProcessingStatus status, Pageable pageable);

    @Query("""
           SELECT e FROM IntelligenceEvent e
           WHERE e.competitor.id = :cid
           AND e.publishedAt BETWEEN :from AND :to
           ORDER BY e.publishedAt DESC
           """)
    List<IntelligenceEvent> findByCompetitorIdAndDateRange(@Param("cid") Long competitorId,
                                                            @Param("from") LocalDateTime from,
                                                            @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(e) FROM IntelligenceEvent e WHERE e.competitor.id = :cid AND e.createdAt >= :since")
    long countRecentByCompetitor(@Param("cid") Long competitorId, @Param("since") LocalDateTime since);

    @Query("""
           SELECT e.category, COUNT(e) FROM IntelligenceEvent e
           WHERE e.competitor.id = :cid AND e.createdAt >= :since
           GROUP BY e.category
           """)
    List<Object[]> countByCategoryForCompetitor(@Param("cid") Long competitorId,
                                                 @Param("since") LocalDateTime since);

    @Query("""
           SELECT e.sentiment, COUNT(e) FROM IntelligenceEvent e
           WHERE e.competitor.id = :cid AND e.createdAt >= :since
           GROUP BY e.sentiment
           """)
    List<Object[]> countBySentimentForCompetitor(@Param("cid") Long competitorId,
                                                   @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(e) FROM IntelligenceEvent e WHERE e.createdAt >= :since")
    long countEventsSince(@Param("since") LocalDateTime since);

    @Query("""
           SELECT e.competitor.id, COUNT(e) FROM IntelligenceEvent e
           WHERE e.createdAt >= :since
           GROUP BY e.competitor.id
           ORDER BY COUNT(e) DESC
           """)
    List<Object[]> findTopCompetitorsByEventCount(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("""
           SELECT e FROM IntelligenceEvent e WHERE
           LOWER(e.title) LIKE LOWER(CONCAT('%',:q,'%')) OR
           LOWER(e.aiSummary) LIKE LOWER(CONCAT('%',:q,'%'))
           """)
    Page<IntelligenceEvent> fullTextSearch(@Param("q") String query, Pageable pageable);

    @Query("""
           SELECT e FROM IntelligenceEvent e
           WHERE e.category = :category
           AND e.sentiment IN :sentiments
           AND e.createdAt >= :since
           ORDER BY e.importanceScore DESC
           """)
    List<IntelligenceEvent> findByCategoryAndSentiments(
            @Param("category") IntelligenceCategory category,
            @Param("sentiments") List<SentimentType> sentiments,
            @Param("since") LocalDateTime since);

    @Query("SELECT e FROM IntelligenceEvent e WHERE e.isFlagged = true ORDER BY e.createdAt DESC")
    Page<IntelligenceEvent> findFlaggedEvents(Pageable pageable);
}
