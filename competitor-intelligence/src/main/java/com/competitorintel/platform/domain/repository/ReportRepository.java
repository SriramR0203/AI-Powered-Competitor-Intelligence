package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findByCreatedByOrderByCreatedAtDesc(String username, Pageable pageable);

    Page<Report> findByCompetitorIdOrderByCreatedAtDesc(Long competitorId, Pageable pageable);

    @Query("SELECT r FROM Report r WHERE r.expiresAt IS NOT NULL AND r.expiresAt < :now")
    List<Report> findExpiredReports(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM Report r ORDER BY r.createdAt DESC")
    Page<Report> findAllOrderByCreatedAtDesc(Pageable pageable);
}
