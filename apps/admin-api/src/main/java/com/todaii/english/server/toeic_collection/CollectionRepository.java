package com.todaii.english.server.toeic_collection;

import com.todaii.english.core.entity.ToeicCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<ToeicCollection, Long> {
    @Query("""
    SELECT c FROM ToeicCollection c
    WHERE (:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    Page<ToeicCollection> search(@Param("keyword") String keyword, Pageable pageable);
}
