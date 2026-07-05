package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);

    List<Tag> findByNameContainingIgnoreCase(String query);

    @Query("SELECT t FROM Tag t ORDER BY SIZE(t.events) DESC")
    List<Tag> findAllOrderByUsageDesc();

    @Query("""
           SELECT t FROM Tag t JOIN t.events e
           WHERE e.competitor.id = :cid
           GROUP BY t ORDER BY COUNT(e) DESC
           """)
    List<Tag> findTopTagsForCompetitor(@Param("cid") Long competitorId);

    /**
     * Find by name or create a new Tag with that name and save it.
     * Not transactional by default — callers must be in a transaction.
     */
    default Tag findOrCreate(String name) {
        return findByName(name).orElseGet(() -> {
            Tag tag = new Tag();
            tag.setName(name.toLowerCase().trim());
            return save(tag);
        });
    }
}
