package com.todaii.english.server.toeic_collection;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.ToeicCollection;

@Repository
public interface CollectionRepository extends JpaRepository<ToeicCollection, Long> {
  @Query("SELECT t FROM ToeicCollection t WHERE t.id = ?1 AND t.isDeleted = false")
  Optional<ToeicCollection> findById(Long id);

  @Query(
      """
              SELECT t FROM ToeicCollection t
              WHERE t.isDeleted = false AND (:keyword IS NULL
                OR STR(t.id) LIKE CONCAT('%', :keyword, '%')
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.alias) LIKE LOWER(CONCAT('%', :keyword, '%')))
              """)
  Page<ToeicCollection> search(@Param("keyword") String keyword, Pageable pageable);

  boolean existsByAlias(String alias);
}
