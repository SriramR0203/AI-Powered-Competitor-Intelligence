package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.ScrapingJob;
import com.competitorintel.platform.domain.enums.ScrapingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapingJobRepository extends JpaRepository<ScrapingJob, Long> {

    Page<ScrapingJob> findBySourceIdOrderByCreatedAtDesc(Long sourceId, Pageable pageable);

    Optional<ScrapingJob> findTopBySourceIdOrderByCreatedAtDesc(Long sourceId);

    List<ScrapingJob> findByStatus(ScrapingStatus status);

    @Query("SELECT j FROM ScrapingJob j WHERE j.competitorId = :cid ORDER BY j.startedAt DESC")
    Page<ScrapingJob> findByCompetitorId(@Param("cid") Long competitorId, Pageable pageable);

    @Query("SELECT COUNT(j) FROM ScrapingJob j WHERE j.status = :status AND j.startedAt >= :since")
    long countByStatusSince(@Param("status") ScrapingStatus status,
                             @Param("since") LocalDateTime since);

    @Query("SELECT AVG(j.durationMs) FROM ScrapingJob j WHERE j.status = :status AND j.completedAt >= :since")
    Double avgDurationMsByStatus(@Param("status") ScrapingStatus status,
                                  @Param("since") LocalDateTime since);

    @Query("SELECT SUM(j.itemsNew) FROM ScrapingJob j WHERE j.completedAt >= :since AND j.status IN ('SUCCESS','PARTIAL_SUCCESS')")
    Long sumNewItemsSince(@Param("since") LocalDateTime since);
}
