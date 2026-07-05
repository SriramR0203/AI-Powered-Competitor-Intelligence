package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.AlertSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertSubscriptionRepository extends JpaRepository<AlertSubscription, Long> {

    List<AlertSubscription> findByAlertRuleId(Long alertRuleId);

    List<AlertSubscription> findByUserId(Long userId);

    Optional<AlertSubscription> findByUserIdAndAlertRuleId(Long userId, Long alertRuleId);

    boolean existsByUserIdAndAlertRuleId(Long userId, Long alertRuleId);

    @Query("""
           SELECT s FROM AlertSubscription s JOIN FETCH s.user
           WHERE s.alertRule.id = :ruleId AND s.emailEnabled = true
           """)
    List<AlertSubscription> findEmailSubscribersForRule(@Param("ruleId") Long ruleId);

    void deleteByUserIdAndAlertRuleId(Long userId, Long alertRuleId);
}
