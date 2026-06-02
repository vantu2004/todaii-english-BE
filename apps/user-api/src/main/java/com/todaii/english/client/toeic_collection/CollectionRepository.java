package com.todaii.english.client.toeic_collection;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicCollection;

@Repository
public interface CollectionRepository extends JpaRepository<ToeicCollection, Long> {
  List<ToeicCollection> findAllByOrderByNameAsc();
}
