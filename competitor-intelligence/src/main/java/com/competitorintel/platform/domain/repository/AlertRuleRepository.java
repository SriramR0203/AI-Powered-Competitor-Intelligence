package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.AlertRule;
import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    List<AlertRule> findByIsActiveTrue();

    Page<AlertRule> findByCompetitorId(Long competitorId, Pageable pageable);

    @Query("""
           SELECT r FROM AlertRule r
           WHERE r.isActive = true
           AND (r.competitor IS NULL OR r.competitor.id = :cid)
           """)
    List<AlertRule> findActiveRulesForCompetitor(@Param("cid") Long competitorId);

    @Query("""
           SELECT r FROM AlertRule r
           WHERE r.isActive = true
           AND (r.categoryFilter IS NULL OR r.categoryFilter = :category)
           """)
    List<AlertRule> findActiveRulesMatchingCategory(@Param("category") IntelligenceCategory category);

    @Query("SELECT COUNT(r) FROM AlertRule r WHERE r.isActive = true")
    long countActiveRules();
}
