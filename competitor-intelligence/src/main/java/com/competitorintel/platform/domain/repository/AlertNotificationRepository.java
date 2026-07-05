package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.AlertNotification;
import com.competitorintel.platform.domain.enums.AlertStatus;
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
public interface AlertNotificationRepository extends JpaRepository<AlertNotification, Long> {

    Page<AlertNotification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<AlertNotification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId,
                                                                        AlertStatus status,
                                                                        Pageable pageable);

    long countByUserIdAndStatus(Long userId, AlertStatus status);

    List<AlertNotification> findByStatus(AlertStatus status);

    @Query("""
           SELECT n FROM AlertNotification n
           WHERE n.status = 'PENDING'
           AND n.alertRule.notifyEmail = true
           ORDER BY n.createdAt ASC
           """)
    List<AlertNotification> findPendingEmailNotifications();

    @Modifying
    @Query("""
           UPDATE AlertNotification n
           SET n.status = 'ACKNOWLEDGED', n.acknowledgedAt = :now
           WHERE n.id = :id AND n.user.id = :userId
           """)
    int acknowledgeNotification(@Param("id") Long id,
                                 @Param("userId") Long userId,
                                 @Param("now") LocalDateTime now);

    @Modifying
    @Query("""
           UPDATE AlertNotification n
           SET n.status = 'ACKNOWLEDGED', n.acknowledgedAt = :now
           WHERE n.user.id = :userId AND n.status = 'PENDING'
           """)
    int acknowledgeAllForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("""
           SELECT COUNT(n) FROM AlertNotification n
           WHERE n.alertRule.id = :ruleId AND n.createdAt >= :since
           """)
    long countRecentByRule(@Param("ruleId") Long ruleId, @Param("since") LocalDateTime since);
}
