package com.todaii.english.server.toeic.collection;

import com.todaii.english.core.entity.ToeicCollection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<ToeicCollection, Long> {

}
