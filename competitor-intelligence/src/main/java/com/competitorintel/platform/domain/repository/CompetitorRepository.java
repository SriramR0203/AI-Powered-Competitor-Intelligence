package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.Competitor;
import com.competitorintel.platform.domain.enums.CompetitorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitorRepository
        extends JpaRepository<Competitor, Long>, JpaSpecificationExecutor<Competitor> {

    Optional<Competitor> findByWebsiteUrl(String websiteUrl);

    boolean existsByWebsiteUrl(String websiteUrl);

    boolean existsByName(String name);

    Page<Competitor> findByStatus(CompetitorStatus status, Pageable pageable);

    List<Competitor> findByStatusOrderByPriorityScoreDesc(CompetitorStatus status);

    Page<Competitor> findByIndustry(String industry, Pageable pageable);

    @Query("SELECT DISTINCT c.industry FROM Competitor c WHERE c.industry IS NOT NULL ORDER BY c.industry")
    List<String> findDistinctIndustries();

    @Query("SELECT COUNT(c) FROM Competitor c WHERE c.status = :status")
    long countByStatus(@Param("status") CompetitorStatus status);

    @Query("SELECT c FROM Competitor c LEFT JOIN FETCH c.sources WHERE c.id = :id")
    Optional<Competitor> findByIdWithSources(@Param("id") Long id);
}
